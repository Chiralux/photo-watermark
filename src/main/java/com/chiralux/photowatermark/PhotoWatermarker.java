package com.chiralux.photowatermark;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PhotoWatermarker {

    public static void run(Path inputPath, WatermarkOptions options) throws IOException {
        if (inputPath == null || !Files.exists(inputPath)) {
            throw new IOException("Path not found: " + inputPath);
        }

        List<File> targets = new ArrayList<>();
        if (Files.isDirectory(inputPath)) {
            // scan for images
            Files.walk(inputPath)
                    .filter(Files::isRegularFile)
                    .filter(p -> {
                        String n = p.getFileName().toString().toLowerCase(Locale.ROOT);
                        return n.endsWith(".jpg") || n.endsWith(".jpeg") || n.endsWith(".png");
                    })
                    .forEach(p -> targets.add(p.toFile()));
        } else {
            String n = inputPath.getFileName().toString().toLowerCase(Locale.ROOT);
            if (n.endsWith(".jpg") || n.endsWith(".jpeg") || n.endsWith(".png")) {
                targets.add(inputPath.toFile());
            } else {
                throw new IOException("Unsupported image format: " + n);
            }
        }

        if (targets.isEmpty()) {
            System.out.println("No images found to process.");
            return;
        }

        int ok = 0, fail = 0, skipped = 0;
        for (File file : targets) {
            try {
                String dateText = extractShootingDate(file);
                if (dateText == null || dateText.isBlank()) {
                    System.out.printf("[skip] %s (no EXIF date)\n", file.getName());
                    skipped++;
                    continue;
                }
                BufferedImage image = ImageIO.read(file);
                if (image == null) {
                    System.out.printf("[skip] %s (cannot read image)\n", file.getName());
                    skipped++;
                    continue;
                }
                BufferedImage marked = drawWatermark(image, dateText, options);
                Path out = resolveOutputPath(file.toPath());
                Files.createDirectories(out.getParent());
                String ext = getExt(file.getName());
                // JPEG does not support alpha channel; convert to RGB when needed
                if (ext.equals("jpg") || ext.equals("jpeg")) {
                    BufferedImage rgb = new BufferedImage(marked.getWidth(), marked.getHeight(), BufferedImage.TYPE_INT_RGB);
                    Graphics2D g = rgb.createGraphics();
                    g.setColor(Color.WHITE);
                    g.fillRect(0, 0, rgb.getWidth(), rgb.getHeight());
                    g.drawImage(marked, 0, 0, null);
                    g.dispose();
                    ImageIO.write(rgb, ext, out.toFile());
                } else {
                    ImageIO.write(marked, ext, out.toFile());
                }
                System.out.printf("[ok] %s -> %s\n", file.getName(), out);
                ok++;
            } catch (Exception e) {
                System.out.printf("[fail] %s: %s\n", file.getName(), e.getMessage());
                fail++;
            }
        }

        System.out.printf("Done. success=%d, skipped=%d, failed=%d\n", ok, skipped, fail);
    }

    private static String getExt(String name) {
        int i = name.lastIndexOf('.');
        return (i > 0) ? name.substring(i + 1).toLowerCase(Locale.ROOT) : "jpg";
    }

    private static Path resolveOutputPath(Path input) {
        Path dir = input.getParent();
        return dir.resolve("_watermark").resolve(input.getFileName());
    }

    private static String extractShootingDate(File imageFile) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
            ExifSubIFDDirectory exif = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (exif != null) {
                Date date = exif.getDateOriginal();
                if (date != null) {
                    return new SimpleDateFormat("yyyy-MM-dd").format(date);
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private static BufferedImage drawWatermark(BufferedImage src, String text, WatermarkOptions options) {
        int w = src.getWidth();
        int h = src.getHeight();
        BufferedImage copy = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = copy.createGraphics();
    g2d.drawImage(src, 0, 0, null);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Prepare font and color
        int fontSize = Math.max(10, options.getFontSize());
        g2d.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        Color color = parseColor(options.getColor());

        // Draw text with outline for readability
        FontMetrics fm = g2d.getFontMetrics();
        int textW = fm.stringWidth(text);
        int textH = fm.getAscent();
        int padding = Math.max(8, fontSize / 2);

        int baselineCenter = (h - (fm.getAscent() + fm.getDescent())) / 2 + fm.getAscent();
        Point pos;
        if (options.getPosition() == WatermarkOptions.Position.LEFT_TOP) {
            pos = new Point(padding, padding + textH);
        } else if (options.getPosition() == WatermarkOptions.Position.CENTER) {
            pos = new Point((w - textW) / 2, baselineCenter);
        } else {
            pos = new Point(w - textW - padding, h - padding);
        }

        // shadow
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString(text, pos.x + 2, pos.y + 2);
        // main text
        g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 230));
        g2d.drawString(text, pos.x, pos.y);

        g2d.dispose();
        return copy;
    }

    private static Color parseColor(String s) {
        if (s == null || s.isBlank()) return Color.WHITE;
        try {
            if (s.startsWith("#")) {
                return Color.decode(s);
            }
            String n = s.toLowerCase(Locale.ROOT);
            if (n.equals("white")) return Color.WHITE;
            if (n.equals("black")) return Color.BLACK;
            if (n.equals("red")) return Color.RED;
            if (n.equals("green")) return Color.GREEN;
            if (n.equals("blue")) return Color.BLUE;
            if (n.equals("yellow")) return Color.YELLOW;
            if (n.equals("gray") || n.equals("grey")) return Color.GRAY;
            return Color.WHITE;
        } catch (Exception e) {
            return Color.WHITE;
        }
    }
}

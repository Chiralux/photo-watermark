package com.chiralux.photowatermark;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    private static void printHelp() {
        System.out.println("photo-watermark 0.1.0\n");
        System.out.println("Usage: java -jar photo-watermark.jar -p <path> [-size <n>] [-color <#RRGGBB|name>] [-pos <left-top|center|right-bottom>]");
        System.out.println("\nOptions:");
        System.out.println("  -p, --path        Image file or directory path (required)");
        System.out.println("  -size, --font-size    Font size, default 24");
        System.out.println("  -color, --font-color  Font color, e.g. #FFFFFF or red, default #FFFFFF");
        System.out.println("  -pos, --position      Watermark position: left-top, center, right-bottom (default right-bottom)");
        System.out.println("  -h, --help        Show this help and exit");
    }

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            printHelp();
            System.exit(0);
        }

        Path path = null;
        int fontSize = 24;
        String color = "#FFFFFF";
        String position = "right-bottom";

        for (int i = 0; i < args.length; i++) {
            String a = args[i];
            if ("-h".equals(a) || "--help".equals(a)) {
                printHelp();
                return;
            } else if ("-p".equals(a) || "--path".equals(a)) {
                if (i + 1 < args.length) {
                    path = Paths.get(args[++i]);
                } else {
                    System.err.println("Missing value for " + a);
                    System.exit(2);
                }
            } else if ("-size".equals(a) || "--font-size".equals(a)) {
                if (i + 1 < args.length) {
                    try {
                        fontSize = Integer.parseInt(args[++i]);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid font size: " + args[i]);
                        System.exit(2);
                    }
                } else {
                    System.err.println("Missing value for " + a);
                    System.exit(2);
                }
            } else if ("-color".equals(a) || "--font-color".equals(a)) {
                if (i + 1 < args.length) {
                    color = args[++i];
                } else {
                    System.err.println("Missing value for " + a);
                    System.exit(2);
                }
            } else if ("-pos".equals(a) || "--position".equals(a)) {
                if (i + 1 < args.length) {
                    position = args[++i];
                } else {
                    System.err.println("Missing value for " + a);
                    System.exit(2);
                }
            } else {
                // ignore unknown for now
            }
        }

        if (path == null) {
            System.err.println("Missing required option: -p <path>");
            printHelp();
            System.exit(2);
        }

        try {
            WatermarkOptions options = new WatermarkOptions(fontSize, color, position);
            PhotoWatermarker.run(path, options);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}

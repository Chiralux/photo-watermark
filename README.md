# photo-watermark

A simple Java CLI to batch add shooting date watermark to photos based on EXIF metadata.

## Build

Requires Java 11+ and Maven.

```pwsh
mvn -q -U -e -DskipTests package
```

Artifacts:
- target/photo-watermark-0.1.0.jar
- target/photo-watermark-0.1.0-jar-with-dependencies.jar (recommended)

## Usage

```pwsh
java -jar target/photo-watermark-0.1.0-jar-with-dependencies.jar `
	-p "C:\photos" `
	-size 24 `
	-color "#FF0000" `
	-pos "right-bottom"
```

Options:
- `-p, --path`    Image file or directory path (required)
- `-size`         Font size, default 24
- `-color`        Font color, e.g. `#FFFFFF`, `red`, default `#FFFFFF`
- `-pos`          Watermark position: `left-top`, `center`, `right-bottom` (default)

Output images are saved to a `_watermark` subfolder under the original directory, with the same filename and format.

Notes:
- If an image has no EXIF original date, it will be skipped.
- Supported formats: jpg, jpeg, png.
- JPEG does not support alpha channel. The tool writes opaque RGB for JPG/JPEG outputs and preserves alpha for PNG.
# Samples

This folder is for manual test samples.

- Put your original photos into `samples/photos/`.
- Run the CLI pointing to that folder. Output images will be generated into `samples/photos/photos_watermark/` (subfolder).

Example (PowerShell):

```pwsh
mvn -DskipTests package
java -jar target/photo-watermark-0.1.0-jar-with-dependencies.jar -p "samples/photos" -size 36 -color "#FF0000" -pos "right-bottom"
```

Then commit both original photos and generated `_watermark` images if you want to share the visual results.

Notes:
- Please make sure you have the right to commit and share the photos.
- Some images may not contain EXIF Original Date and will be skipped.

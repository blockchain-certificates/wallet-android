package com.learningmachine.android.app.util;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;

public class ImageUtils {

    private static final String IMAGE_FILE_EXT = ".png";
    private static final String JSON_SEPARATOR = ",";

    public static String getImageFilename(String uuid) {
        String hash = StringUtils.md5(uuid);
        if (StringUtils.isEmpty(hash)) {
            return null;
        }
        return hash + IMAGE_FILE_EXT;
    }

    public static File getImageFile(Context context, String uuid) {
        String filename = ImageUtils.getImageFilename(uuid);

        if (StringUtils.isEmpty(filename)) {
            return null;
        }

        File filesDir = context.getFilesDir();
        return new File(filesDir, filename);
    }

    public static String getImageDataFromJson(String jsonData) {
        String[] parts = jsonData.split(JSON_SEPARATOR);
        if (parts.length != 2) {
            return null;
        }
        return parts[1];
    }

    /**
     * Search for transparent pixel in 4 corners of a Bitmap
     * @param bmp The bitmap to search for a transparent pixel
     * @return True if at least one corner is transparent.
     */
    public static boolean hasTransparentPixel(Bitmap bmp) {
        int top = 0;
        int left = 0;
        int right = bmp.getWidth() - 1;
        int bottom = bmp.getHeight() - 1;
        //Int for transparent is 0
        int pixel = bmp.getPixel(left, top) & bmp.getPixel(left, bottom) &
                bmp.getPixel(right, top) & bmp.getPixel(right, bottom);
        int transparent = 0;
        return pixel == transparent;
    }
}

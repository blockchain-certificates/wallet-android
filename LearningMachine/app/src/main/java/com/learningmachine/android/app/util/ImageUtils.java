package com.learningmachine.android.app.util;

import android.content.Context;

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
}

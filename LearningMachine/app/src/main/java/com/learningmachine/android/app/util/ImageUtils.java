package com.learningmachine.android.app.util;

public class ImageUtils {

    private static final String IMAGE_FILE_EXT = ".png";

    public static String getIssuerImageFilename(String uuid) {
        String hash = StringUtils.md5(uuid);
        if (StringUtils.isEmpty(hash)) {
            return null;
        }
        return hash + IMAGE_FILE_EXT;
    }
}

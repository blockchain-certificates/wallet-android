package com.learningmachine.android.app.util;

import android.content.Context;
import android.widget.ImageView;

import com.learningmachine.android.app.data.model.Issuer;
import com.squareup.picasso.Picasso;

import java.io.File;

public class ImageUtils {

    private static final String IMAGE_FILE_EXT = ".png";
    private static final String JSON_SEPARATOR = ",";
    private static final String PATH_SEPARATOR = "/";

    public static String getIssuerImageFilename(String uuid) {
        String hash = StringUtils.md5(uuid);
        if (StringUtils.isEmpty(hash)) {
            return null;
        }
        return hash + IMAGE_FILE_EXT;
    }

    public static String getImageDataFromJson(String jsonData) {
        String[] parts = jsonData.split(JSON_SEPARATOR);
        if (parts.length != 2) {
            return null;
        }
        return parts[1];
    }

    public static void loadIssuerImageView(Context context, Issuer issuer, ImageView imageView) {
        if (issuer == null || imageView == null) {
            return;
        }

        String uuid = issuer.getUuid();
        String filename = ImageUtils.getIssuerImageFilename(uuid);

        if (StringUtils.isEmpty(filename)) {
            return;
        }

        File filesDir = context.getFilesDir();
        File file = new File(filesDir, filename);

        Picasso.with(context)
                .load(file)
                .into(imageView);
    }
}

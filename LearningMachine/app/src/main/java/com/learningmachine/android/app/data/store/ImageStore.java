package com.learningmachine.android.app.data.store;

import android.content.Context;
import android.util.Base64;

import com.learningmachine.android.app.util.ImageUtils;
import com.learningmachine.android.app.util.StringUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import timber.log.Timber;

public class ImageStore implements DataStore {

    private Context mContext;

    public ImageStore(Context context) {
        mContext = context;
    }

    /**
     * @param uuid     Issuer url
     * @param jsonData Image data
     * @return true if the image was written to file successfully
     */
    public boolean saveImage(String uuid, String jsonData) {
        if (StringUtils.isEmpty(uuid) || StringUtils.isEmpty(jsonData)) {
            return false;
        }

        String filename = ImageUtils.getImageFilename(uuid);
        if (StringUtils.isEmpty(filename)) {
            return false;
        }

        String imageData = ImageUtils.getImageDataFromJson(jsonData);
        if (StringUtils.isEmpty(imageData)) {
            return false;
        }

        boolean success = false;

        try (FileOutputStream fileOutputStream = mContext.openFileOutput(filename, Context.MODE_PRIVATE)) {
            byte[] decodedString = Base64.decode(imageData, Base64.DEFAULT);
            fileOutputStream.write(decodedString);
            fileOutputStream.flush();
            success = true;
        } catch (FileNotFoundException e) {
            Timber.e(e, "Unable to open file");
        } catch (IOException e) {
            Timber.e(e, "Unable to write to file");
        }

        return success;
    }

    @Override
    public void reset() {
        String[] fileList = mContext.fileList();
        for (String file : fileList) {
            if (file.contains(".png")) {
                mContext.deleteFile(file);
            }
        }
    }
}

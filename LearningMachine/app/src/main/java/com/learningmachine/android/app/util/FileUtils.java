package com.learningmachine.android.app.util;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import timber.log.Timber;

public class FileUtils {

    private static final String CERT_DIR = "certs";
    private static final String JSON_EXT = ".json";

    public static boolean saveCertificate(Context context, ResponseBody responseBody, String uuid) {
        File dir = new File(context.getFilesDir(), CERT_DIR);
        dir.mkdirs();
        String filename = uuid + JSON_EXT;
        File file = new File(dir, filename);
        return writeResponseBodyToDisk(file, responseBody);
    }

    private static boolean writeResponseBodyToDisk(File file, ResponseBody body) {
        try (OutputStream outputStream = new FileOutputStream(file)) {

            InputStream inputStream = body.byteStream();

            byte[] fileReader = new byte[4096];
            long fileSize = body.contentLength();
            long fileSizeDownloaded = 0;

            while (true) {
                int read = inputStream.read(fileReader);

                if (read == -1) {
                    break;
                }

                outputStream.write(fileReader, 0, read);

                fileSizeDownloaded += read;

                Timber.d("file download: " + fileSizeDownloaded + " of " + fileSize);
            }

            outputStream.flush();

            return true;
        } catch (IOException e) {
            Timber.e(e, "Unable to write ResponseBody to file");
            return false;
        }
    }
}

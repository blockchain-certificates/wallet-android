package com.learningmachine.android.app.util;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okio.Buffer;
import timber.log.Timber;

public class FileUtils {

    private static final String CERT_DIR = "certs";
    private static final String JSON_EXT = ".json";

    public static boolean saveCertificate(Context context, Buffer buffer, String uuid) {
        File file = getCertificateFile(context, uuid, true);
        return writeResponseBodyToDisk(file, buffer);
    }

    public static File getCertificateFile(Context context, String uuid) {
        return getCertificateFile(context, uuid, false);
    }

    private static File getCertificateFile(Context context, String uuid, boolean createDir) {
        File certDir = new File(context.getFilesDir(), CERT_DIR);
        if (createDir) {
            certDir.mkdirs();
        }
        String filename = uuid + JSON_EXT;
        return new File(certDir, filename);
    }

    private static boolean writeResponseBodyToDisk(File file, Buffer buffer) {
        try (OutputStream outputStream = new FileOutputStream(file)) {

            InputStream inputStream = buffer.inputStream();

            byte[] fileReader = new byte[4096];
            long fileSize = buffer.size();
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

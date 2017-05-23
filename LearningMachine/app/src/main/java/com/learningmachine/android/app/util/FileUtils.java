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

    public static boolean copyCertificateStream(Context context, InputStream inputStream, String uuid) {
        File outputFile = getCertificateFile(context, uuid, true);
        return copyInputStream(inputStream, outputFile);
    }

    public static boolean deleteCertificate(Context context, String uuid) {
        File file = getCertificateFile(context, uuid);
        return file.delete();
    }

    public static File getCertificateFile(Context context, String uuid) {
        return getCertificateFile(context, uuid, false);
    }

    private static File getCertificateFile(Context context, String uuid, boolean createDir) {
        File certDir = getCertificateDirectory(context, createDir);
        String filename = uuid + JSON_EXT;
        return new File(certDir, filename);
    }

    private static File getCertificateDirectory(Context context, boolean createDir) {
        File certDir = new File(context.getFilesDir(), CERT_DIR);
        if (createDir) {
            certDir.mkdirs();
        }
        return certDir;
    }

    private static boolean writeResponseBodyToDisk(File file, Buffer buffer) {
        try (InputStream inputStream = buffer.inputStream();
             OutputStream outputStream = new FileOutputStream(file)) {
            return copyStreams(inputStream, outputStream);
        } catch (IOException e) {
            Timber.e(e, "Unable to write ResponseBody to file");
            return false;
        }
    }

    private static boolean copyInputStream(InputStream inputStream, File outputFile) {
        try (OutputStream outputStream = new FileOutputStream(outputFile)) {
            return copyStreams(inputStream, outputStream);
        } catch (IOException e) {
            Timber.e(e, "Unable to copy certificate file");
            return false;
        }
    }

    private static boolean copyStreams(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[4096];
        while (true) {
            int read = inputStream.read(buffer);
            if (read == -1) {
                break;
            }
            outputStream.write(buffer, 0, read);
        }

        outputStream.flush();
        return true;
    }
}

package com.learningmachine.android.test.helpers;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.google.gson.Gson;

public class FileHelpers {
    public static String readFileAsString (String filePath) throws Exception {
        final String dir = System.getProperty("user.dir");
        final String absolutePath = dir + filePath;
        return new String(Files.readAllBytes(Paths.get(absolutePath)));
    }

    public static <T> T readFileAsClass (String filePath, Class TargetClass) {
        try {
            String fileContent = readFileAsString(filePath);
            Gson gson = new Gson();
            return (T) gson.fromJson(fileContent, TargetClass);
        } catch (Exception e) {
            throw new RuntimeException (e);
        }
    }

    public static InputStream getResourceAsStream(String name, ClassLoader classLoader) {
        InputStream inputStream = classLoader.getResourceAsStream(name);
        return inputStream;
    }
}

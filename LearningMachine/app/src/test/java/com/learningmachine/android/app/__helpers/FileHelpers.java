package com.learningmachine.android.test.helpers;

import java.nio.file.Files;
import java.nio.file.Paths;

public class FileHelpers {
    public static String readFileAsString (String filePath) throws Exception {
        final String dir = System.getProperty("user.dir");
        final String absolutePath = dir + filePath;
        return new String(Files.readAllBytes(Paths.get(absolutePath)));
    }
}

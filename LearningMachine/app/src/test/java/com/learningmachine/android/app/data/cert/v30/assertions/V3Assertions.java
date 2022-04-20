package com.learningmachine.android.test.data.cert.v30;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.nio.file.Files;
import java.nio.file.Paths;

public class V3Assertions {
    public static String getBase64PngValue () {
        try {
            final String dir = System.getProperty("user.dir");
            final String blockcertString = readFileAsString(dir + "/src/test/resources/v3/testnet-display-png.json");
            JsonObject blockcertAsJsonObject = new Gson().fromJson(blockcertString, JsonObject.class);
            return blockcertAsJsonObject
                    .get("display").getAsJsonObject()
                    .get("content").getAsString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String readFileAsString (String filePath) throws Exception {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }
}

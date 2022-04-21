package com.learningmachine.android.test.data.cert.v30;

import com.learningmachine.android.test.helpers.FileHelpers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class V3Assertions {
    public static String getBase64PngValue () {
        try {
            final String blockcertString = FileHelpers.readFileAsString("/src/test/resources/v3/testnet-display-png.json");
            JsonObject blockcertAsJsonObject = new Gson().fromJson(blockcertString, JsonObject.class);
            return blockcertAsJsonObject
                    .get("display").getAsJsonObject()
                    .get("content").getAsString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static String getBase64PDFValue () {
        try {
            final String blockcertString = FileHelpers.readFileAsString("/src/test/resources/v3/testnet-display-pdf.json");
            JsonObject blockcertAsJsonObject = new Gson().fromJson(blockcertString, JsonObject.class);
            return blockcertAsJsonObject
                    .get("display").getAsJsonObject()
                    .get("content").getAsString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

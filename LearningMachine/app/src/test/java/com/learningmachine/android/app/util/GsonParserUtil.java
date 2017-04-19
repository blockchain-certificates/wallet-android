package com.learningmachine.android.app.util;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;

public class GsonParserUtil {

    public static final String ISSUER_STARK = "issuer-stark";

    public Object loadModelObject(String file, Class clazz) throws Exception {
        String filename = file + ".json";

        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream(filename);
        InputStreamReader reader = new InputStreamReader(inputStream);

        Gson gson = new Gson();
        return gson.fromJson(reader, clazz);
    }
}

package com.learningmachine.android.app.data.cert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;

public class BlockCertParser {
    private Gson mGson;

    public BlockCertParser() {
        mGson = new GsonBuilder()
                .registerTypeAdapter(BlockCert.class, new BlockCertAdapter())
                .create();
    }

    public BlockCert fromJson(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        return mGson.fromJson(inputStreamReader, BlockCert.class);
    }

    public BlockCert fromJson(String string) {
        return mGson.fromJson(string, BlockCert.class);
    }
}

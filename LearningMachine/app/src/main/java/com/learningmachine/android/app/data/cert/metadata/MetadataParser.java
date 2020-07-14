package com.learningmachine.android.app.data.cert.metadata;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.learningmachine.android.app.R;

import java.io.Reader;
import java.text.NumberFormat;

public class MetadataParser {
    private Gson mGson;

    public MetadataParser(Context context) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        NumberFormat integerFormat = NumberFormat.getIntegerInstance();
        String trueString = context.getString(R.string.cert_metadata_boolean_true);
        String falseString = context.getString(R.string.cert_metadata_boolean_false);
        MetadataTypeAdapter metadataTypeAdapter = new MetadataTypeAdapter(numberFormat, integerFormat, trueString, falseString);
        PropertyTypeTypeAdapter propertyTypeTypeAdapter = new PropertyTypeTypeAdapter();
        mGson = new GsonBuilder()
                .registerTypeAdapter(Metadata.class, metadataTypeAdapter)
                .registerTypeAdapter(PropertyType.class, propertyTypeTypeAdapter)
                .create();
    }

    public Metadata fromJson(String string) {
        return mGson.fromJson(string, Metadata.class);
    }

    public Metadata fromJson(Reader reader) {
        return mGson.fromJson(reader, Metadata.class);
    }
}

package com.hyland.android.app.data.cert.metadata;

public class Field {
    private String mTitle;
    private String mValue;

    public Field(String title, String value) {
        mTitle = title;
        mValue = value;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getValue() {
        return mValue;
    }
}

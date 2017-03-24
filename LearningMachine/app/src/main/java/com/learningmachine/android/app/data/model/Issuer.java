package com.learningmachine.android.app.data.model;

public class Issuer {

    private String mName;
    private int mImageResId;

    public Issuer(String name, int imageResId) {
        mName = name;
        mImageResId = imageResId;
    }

    public String getName() {
        return mName;
    }

    public int getImageResId() {
        return mImageResId;
    }
}

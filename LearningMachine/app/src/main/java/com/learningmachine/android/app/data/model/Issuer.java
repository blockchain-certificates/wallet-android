package com.learningmachine.android.app.data.model;

import java.io.Serializable;

public class Issuer implements Serializable {

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

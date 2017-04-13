package com.learningmachine.android.app.data.model;

import java.io.Serializable;

public class Certificate implements Serializable {

    private String mName;
    private String mDescription;

    public Certificate(String name, String description) {
        mName = name;
        mDescription = description;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }
}

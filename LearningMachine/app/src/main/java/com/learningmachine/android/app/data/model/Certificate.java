package com.learningmachine.android.app.data.model;

import com.google.gson.annotations.SerializedName;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;

import java.io.Serializable;

public class Certificate implements Serializable {

    @SerializedName("language")
    private String mLanguage;
    @SerializedName("id")
    private String mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("subtitle")
    private String mSubtitle;
    @SerializedName("issuer")
    private IssuerResponse mIssuerResponse;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("type")
    private String mType;

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

package com.learningmachine.android.app.data.model;

import com.google.gson.annotations.SerializedName;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;

import java.io.Serializable;

public class Certificate implements Serializable {

    @SerializedName("language")
    private String mLanguage;
    @SerializedName("id")
    private String mUuid;
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

    public String getLanguage() {
        return mLanguage;
    }

    public void setLanguage(String language) {
        mLanguage = language;
    }

    public String getUuid() {
        return mUuid;
    }

    public void setUuid(String uuid) {
        mUuid = uuid;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getSubtitle() {
        return mSubtitle;
    }

    public void setSubtitle(String subtitle) {
        mSubtitle = subtitle;
    }

    public IssuerResponse getIssuerResponse() {
        return mIssuerResponse;
    }

    public void setIssuerResponse(IssuerResponse issuerResponse) {
        mIssuerResponse = issuerResponse;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }
}

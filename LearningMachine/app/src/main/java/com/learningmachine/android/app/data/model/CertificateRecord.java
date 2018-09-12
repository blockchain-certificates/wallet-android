package com.learningmachine.android.app.data.model;

import com.google.gson.annotations.SerializedName;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;
import com.learningmachine.android.app.util.StringUtils;

public class CertificateRecord {

    @SerializedName("language") private String mLanguage;
    @SerializedName("name") private String mName;
    @SerializedName("subtitle") private String mSubtitle;
    @SerializedName("issuer") private IssuerResponse mIssuerResponse;
    @SerializedName("description") private String mDescription;
    @SerializedName("type") private String mType;

    // Must be set manually
    private String mUuid;
    private String mIssuerUuid;
    private String mIssuedOn;
    private String mUrlString;
    private String mMetadata;
    private String mExpirationDate;

    public CertificateRecord(String uuid, String issuerUuid, String name, String description,
                             String issuedOn, String urlString, String metadata, String expirationDate) {
        mUuid = uuid;
        mIssuerUuid = issuerUuid;
        mName = name;
        mDescription = description;
        mIssuedOn = issuedOn;
        mUrlString = urlString;
        mMetadata = metadata;
        mExpirationDate = expirationDate;
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

    public String getIssuerUuid() {
        if (!StringUtils.isEmpty(mIssuerUuid)) {
            return mIssuerUuid;
        } else if (mIssuerResponse != null) {
            return mIssuerResponse.getUuid();
        }
        return null;
    }

    public void setIssuerUuid(String issuerUuid) {
        mIssuerUuid = issuerUuid;
    }

    public String getIssuedOn() {
        return mIssuedOn;
    }

    public void setIssuedOn(String issuedOn) {
        mIssuedOn = issuedOn;
    }

    public String getUrlString() {
        return mUrlString;
    }

    public void setUrlString(String urlString) {
        mUrlString = urlString;
    }

    public String getMetadata() {
        return mMetadata;
    }

    public boolean urlStringContainsUrl() {
        return StringUtils.isWebUrl(mUrlString);
    }

    public String getExpirationDate() {
        return mExpirationDate;
    }
}

package com.hyland.android.app.data.cert.v30;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.hyland.android.app.data.cert.BlockCert;
import com.hyland.android.app.data.webservice.response.IssuerResponse;
import com.hyland.android.app.util.StringUtils;
import com.hyland.android.app.LMConstants;

import timber.log.Timber;

public class BlockCertV30 implements BlockCert {
    private JsonObject mDocumentNode;

    @SerializedName("display")
    @Expose
    private JsonElement mDisplay;

    @SerializedName("issuer")
    @Expose
    private JsonElement mIssuer;

    @SerializedName("id")
    @Expose
    private String mUid;

    @SerializedName("name")
    @Expose
    private JsonElement mName;

    @SerializedName("description")
    @Expose
    private JsonElement mDescription;

    @SerializedName("credentialSubject")
    @Expose
    private JsonElement mCredentialSubject;

    @SerializedName("credentialStatus")
    @Expose
    private JsonObject mCredentialStatus;

    @SerializedName("issuanceDate")
    @Expose
    private String mIssuanceDate;


    @SerializedName("expirationDate")
    @Expose
    private String mExpirationDate;

    // v3.2 (VC v2.0)
    @SerializedName("validFrom")
    @Expose
    private String mValidFrom;

    // v3.2 (VC v2.0)
    @SerializedName("validUntil")
    @Expose
    private String mValidUntil;

    @SerializedName("metadata")
    @Expose
    private String mMetadata;

    public String version () {
        return "v3";
    }

    @Override
    public JsonObject getDocumentNode() {
        return mDocumentNode;
    }

    public void setDocumentNode(JsonObject documentNode) {
        mDocumentNode = documentNode;
    }

    @Override
    public String getDisplayHtml() {
        final JsonObject displayAsJsonObject = mDisplay.getAsJsonObject();
        final String contentMediaType = displayAsJsonObject.get("contentMediaType").getAsString();
        final String content = displayAsJsonObject.get("content").getAsString();
        String contentEncoding = "";
        if (displayAsJsonObject.has("contentEncoding")) {
            contentEncoding = displayAsJsonObject.get("contentEncoding").getAsString();
        }

        switch (contentMediaType) {
            case "text/html":
                return content;

            case "image/png":
            case "image/jpeg":
            case "image/gif":
            case "image/bmp":
                return "<img src=\"data:" + contentMediaType + ";" + contentEncoding + "," + content + "\"/>";

            case "application/pdf":
                return "<embed width=\"100%\" height=\"100%\" type=\"application/pdf\" src=\"data:" + contentMediaType + ";" + contentEncoding + "," + content + "\"/>";

            default:
                return "No readable content";
        }

    }

    @Override
    public IssuerResponse getIssuer() {
        // not implemented
        return null;
    }

    public JsonElement getIssuerProfile() {
        return mIssuer;
    }

    public JsonObject getCredentialSubject() {
        if (mCredentialSubject.isJsonArray()) {
            // TODO: definitely not ideal as multiple credential subjects could mean different things
            // for now we are just assuming multilingual support, and defaulting to the first item
            return mCredentialSubject.getAsJsonArray().get(0).getAsJsonObject();
        }
        return (JsonObject) mCredentialSubject;
    }

    public JsonObject getClaim() {
        if (getCredentialSubject().get("claim") != null && getCredentialSubject().get("claim").isJsonObject()) {
            return getCredentialSubject().get("claim").getAsJsonObject();
        }
        return null;

    }

    @Override
    public String getExpirationDate() {
        if (mValidUntil != null) {
            return mValidUntil;
        }
        return mExpirationDate;
    }


    @Override
    public String getCertUid() {
        return mUid;
    }

    @Override
    public String getCertName() {
        if (mName != null) {
            if (mName.isJsonArray()) {
                // TODO: deal with OS/app language
                return getPropertyValueForLanguage(mName.getAsJsonArray(), "en");
            }

            if (mName.isJsonPrimitive() && mName.getAsJsonPrimitive().isString()) {
                return mName.getAsString();
            }
        }

        if (getClaim() == null) {
            return null;
        }
        if (getClaim().get("name") == null) {
            return null;
        }
        return getClaim().get("name").getAsString();
    }

    @Override
    public String getCertDescription() {
        if (mDescription != null) {
            if (mDescription.isJsonArray()) {
                // TODO: deal with OS/app language
                return getPropertyValueForLanguage(mDescription.getAsJsonArray(), "en");
            }

            if (mDescription.isJsonPrimitive() && mDescription.getAsJsonPrimitive().isString()) {
                return mDescription.getAsString();
            }
        }

        if (getClaim() == null) {
            return null;
        }
        if (getClaim().get("description") == null) {
            return null;
        }
        return getClaim().get("description").getAsString();
    }

    @Override
    public String getIssuerId() {
        if (getIssuerProfile().isJsonObject()) {
            return getIssuerProfile().getAsJsonObject().get("id").getAsString();
        }
        return getIssuerProfile().getAsString();
    }

    @Override
    public String getIssueDate() {
        if (mValidFrom != null) {
            return mValidFrom;
        }
        return mIssuanceDate;
    }

    @Override
    public String getUrl() {
        if (StringUtils.isWebUrl(getCertUid())) {
            return getCertUid();
        }
        return null;
    }

    @Override
    public String getRecipientPublicKey() {
        if (getCredentialSubject().get("publicKey") == null) {
            return null;
        }
        String keyString = getCredentialSubject().get("publicKey").getAsString();
        if (keyString.startsWith(LMConstants.ECDSA_KOBLITZ_PUBKEY_PREFIX)) {
            keyString = keyString.substring(LMConstants.ECDSA_KOBLITZ_PUBKEY_PREFIX.length());
        }
        return keyString;
    }

    @Override
    public String getVerificationPublicKey() {
        return "Not implemented";
    }

    @Override
    public String getSourceId() {
        return "Not implemented";
    }

    @Override
    public String getMerkleRoot() {
        return "Not implemented";
    }

    @Override
    public String getMetadata() {
        return mMetadata;
    }

    @Override
    public String getReceiptHash() {
        return "Not implemented";
    }

    private String getPropertyValueForLanguage(JsonArray property, String language) {
        for (JsonElement element : property) {
            if (element.getAsJsonObject().get("@language").getAsString().equals(language)) {
                return element.getAsJsonObject().get("@value").getAsString();
            }
        }
        return "";
    }
}

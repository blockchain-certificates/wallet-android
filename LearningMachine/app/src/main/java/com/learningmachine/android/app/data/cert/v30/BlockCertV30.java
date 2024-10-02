package com.learningmachine.android.app.data.cert.v30;

import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;
import com.learningmachine.android.app.util.StringUtils;
import com.learningmachine.android.app.LMConstants;

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

    @SerializedName("credentialSubject")
    @Expose
    private JsonObject mCredentialSubject;

    @SerializedName("credentialStatus")
    @Expose
    private JsonObject mCredentialStatus;

    @SerializedName("issuanceDate")
    @Expose
    private String mIssuanceDate;

    @SerializedName("expirationDate")
    @Expose
    private String mExpirationDate;

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
        return mCredentialSubject;
    }

    public JsonObject getClaim() {
        if (getCredentialSubject().get("claim") != null) {
            return getCredentialSubject().get("claim").getAsJsonObject();
        }
        return null;

    }

    @Override
    public String getExpirationDate() {
        return mExpirationDate;
    }


    @Override
    public String getCertUid() {
        return mUid;
    }

    @Override
    public String getCertName() {
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
}

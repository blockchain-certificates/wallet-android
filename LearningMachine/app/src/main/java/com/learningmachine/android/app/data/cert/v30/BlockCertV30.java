package com.learningmachine.android.app.data.cert.v30;

import com.google.gson.JsonObject;

import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;

public class BlockCertV30 implements BlockCert {
    private JsonObject mDocumentNode;

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
    public IssuerResponse getIssuer() {
        // Not implemented
        return null;
    }

    @Override
    public String getDisplayHtml() {
        return "Not implemented";
    }

    @Override
    public String getExpirationDate() {
        return "Not implemented";
    }


    @Override
    public String getCertUid() {
        return "Not implemented";
    }

    @Override
    public String getCertName() {
        return "Not implemented";
    }

    @Override
    public String getCertDescription() {
        return "Not implemented";
    }

    @Override
    public String getIssuerId() {
        return "Not implemented";
    }

    @Override
    public String getIssueDate() {
        return "Not implemented";
    }

    @Override
    public String getUrl() {
        return "Not implemented";
    }

    @Override
    public String getRecipientPublicKey() {
        return "Not implemented";
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
        return "Not implemented";
    }

    @Override
    public String getReceiptHash() {
        return "Not implemented";
    }
}

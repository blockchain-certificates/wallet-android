package com.learningmachine.android.app.data.cert.v11;

import com.google.gson.JsonObject;
import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;

import org.joda.time.DateTime;

public class BlockCertV11 extends CertificateSchemaV11 implements BlockCert {
    private JsonObject mDocumentNode;

    @Override
    public String getCertUid() {
        if (getAssertion() == null) {
            return null;
        }
        return getAssertion().getUid();
    }

    @Override
    public String getCertName() {
        return getCertificate().getTitle();
    }

    @Override
    public String getCertDescription() {
        return getCertificate().getDescription();
    }

    @Override
    public String getIssuerId() {
        return null;
    }

    @Override
    public String getIssueDate() {
        return getAssertion().getIssuedOn()
                .toString();
    }

    @Override
    public String getExpirationDate() {
        return null;
    }

    @Override
    public String getUrl() {
        return getAssertion().getId()
                .toString();
    }

    @Override
    public String getRecipientPublicKey() {
        return getRecipient().getPubkey();
    }

    @Override
    public String getVerificationPublicKey() {
        return null;
    }

    @Override
    public String getSourceId() {
        return null;
    }

    @Override
    public String getMerkleRoot() {
        return null;
    }

    @Override
    public String getMetadata() {
        return null;
    }

    @Override
    public IssuerResponse getIssuer() {
        if (getCertificate() == null
                || getCertificate().getIssuer() == null) {
            return null;
        }

        Issuer issuer = getCertificate().getIssuer();
        String name = issuer.getName();
        String email = issuer.getEmail();
        String certUuid = issuer.getId().toString();
        String certsUrl = null;
        String introUrl = null;
        String introducedOn = DateTime.now().toString();
        String imageData = issuer.getImage();
        String analytics = null;

        return new IssuerResponse(name, email, null, certUuid, certsUrl, introUrl, introducedOn, imageData, analytics);
    }

    @Override
    public JsonObject getDocumentNode() {
        return mDocumentNode;
    }

    @Override
    public String getReceiptHash() {
        return null;
    }

    public void setDocumentNode(JsonObject documentNode) {
        mDocumentNode = documentNode;
    }
}

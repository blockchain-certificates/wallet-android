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
        return getAssertion().getIssuedOn().toString();
    }

    @Override
    public String getUrl() {
        return getAssertion().getId().toString();
    }

    @Override
    public String getRecipientPublicKey() {
        return getRecipient().getPubkey();
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
    public IssuerResponse getIssuer() {
        if (getCertificate() == null
                || getCertificate().getIssuer() == null) {
            return null;
        }
        Issuer issuer = getCertificate().getIssuer();
        String name = issuer.getName();
        String email = issuer.getEmail();
        String certUuid = issuer.getId().toString();
        String certUrl = null;
        String introUrl = null;
        String introducedOn = DateTime.now().toString();
        String imageData = issuer.getImage();
        IssuerResponse issuerResponse = new IssuerResponse(name, email, certUuid, certUrl, introUrl, introducedOn, imageData);
        return issuerResponse;
    }

    @Override
    public JsonObject getDocumentNode() {
        return mDocumentNode;
    }

    public void setDocumentNode(JsonObject documentNode) {
        mDocumentNode = documentNode;
    }
}

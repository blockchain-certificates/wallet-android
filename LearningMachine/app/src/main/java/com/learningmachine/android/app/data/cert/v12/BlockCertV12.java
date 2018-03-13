package com.learningmachine.android.app.data.cert.v12;

import com.google.gson.JsonObject;
import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;
import com.learningmachine.android.app.util.ListUtils;

import org.joda.time.DateTime;

import java.net.URI;
import java.net.URISyntaxException;

public class BlockCertV12 extends BlockchainCertificate implements BlockCert {
    private JsonObject mDocumentNode;

    public static BlockCertV12 createInstance(String certUuid, String issuerUuid, String name, String description, String issuedDate, String urlString) throws URISyntaxException {
        BlockCertV12 blockCert = new BlockCertV12();
        Assertion assertion = new Assertion();
        assertion.setUid(certUuid);
        assertion.setIssuedOn(issuedDate);
        assertion.setId(new URI(urlString));
        Issuer issuer = new Issuer();
        issuer.setId(new URI(issuerUuid));

        Certificate certificate = new com.learningmachine.android.app.data.cert.v12.Certificate();
        certificate.setIssuer(issuer);
        certificate.setName(name);
        certificate.setDescription(description);

        Document document = new Document();
        document.setAssertion(assertion);
        document.setCertificate(certificate);
        blockCert.setDocument(document);
        return blockCert;
    }

    @Override
    public String getCertUid() {
        if (getDocument() == null
                || getDocument().getAssertion() == null) {
            return null;
        }
        return getDocument().getAssertion()
                .getUid();
    }

    @Override
    public String getCertName() {
        if (getDocument() == null
                || getDocument().getCertificate() == null) {
            return null;
        }
        return getDocument().getCertificate()
                .getName();
    }

    @Override
    public String getCertDescription() {
        if (getDocument() == null
                || getDocument().getCertificate() == null) {
            return null;
        }
        return getDocument().getCertificate()
                .getDescription();
    }

    @Override
    public String getIssuerId() {
        if (getDocument() == null
                || getDocument().getCertificate() == null
                || getDocument().getCertificate().getIssuer() == null
                || getDocument().getCertificate().getIssuer().getId() == null) {
            return null;
        }
        return getDocument().getCertificate()
                .getIssuer()
                .getId()
                .toString();
    }

    @Override
    public String getIssueDate() {
        if (getDocument() == null
                || getDocument().getAssertion() == null) {
            return null;
        }
        return getDocument().getAssertion()
                .getIssuedOn();
    }

    @Override
    public String getExpirationDate() {
        return null;
    }

    @Override
    public String getUrl() {
        if (getDocument() == null
                || getDocument().getAssertion() == null
                || getDocument().getAssertion().getId() == null) {
            return null;
        }
        return getDocument().getAssertion()
                .getId()
                .toString();
    }

    @Override
    public String getRecipientPublicKey() {
        if (getDocument() == null
                || getDocument().getRecipient() == null) {
            return null;
        }
        return getDocument().getRecipient()
                .getPublicKey();
    }

    @Override
    public String getVerificationPublicKey() {
        return null;
    }

    @Override
    public String getSourceId() {
        if (getReceipt() == null
                || ListUtils.isEmpty(getReceipt().getAnchors())) {
            return null;
        }
        return getReceipt().getAnchors()
                .get(0)
                .getSourceId();
    }

    @Override
    public String getMerkleRoot() {
        if (getReceipt() == null) {
            return null;
        }
        return getReceipt().getMerkleRoot();
    }

    @Override
    public String getMetadata() {
        return null;
    }

    @Override
    public IssuerResponse getIssuer() {
        if (getDocument() == null
                || getDocument().getCertificate() == null
                || getDocument().getCertificate().getIssuer() == null) {
            return null;
        }

        Issuer issuer = getDocument().getCertificate().getIssuer();
        String name = issuer.getName();
        String email = issuer.getEmail();
        String certUuid = issuer.getId().toString();
        String certsUrl = null;
        String introUrl = null;
        String introducedOn = DateTime.now().toString();
        String analytics = null;
        String imageData = issuer.getImage();

        return new IssuerResponse(name, email, null, certUuid, certsUrl, introUrl, introducedOn, imageData, analytics);

    }

    @Override
    public JsonObject getDocumentNode() {
        return mDocumentNode;
    }

    @Override
    public String getReceiptHash() {
        return getReceipt().getTargetHash();
    }

    public void setDocumentNode(JsonObject documentNode) {
        mDocumentNode = documentNode;
    }
}

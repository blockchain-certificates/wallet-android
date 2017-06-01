package com.learningmachine.android.app.data.cert.v20;

import com.google.gson.JsonObject;
import com.learningmachine.android.app.LMConstants;
import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;
import com.learningmachine.android.app.util.ListUtils;

import org.joda.time.DateTime;

public class BlockCertV20 extends CertSchemaV20 implements BlockCert {
    private static final String URN_PREFIX = "urn:uuid:";

    private JsonObject mDocumentNode;

    @Override
    public String getCertUid() {
        if (getBadge() == null
                || getBadge().getId() == null) {
            return null;
        }
        String idString = getBadge().getId().toString();
        if (idString.startsWith(URN_PREFIX)) {
            idString = idString.substring(URN_PREFIX.length());
        }
        return idString;
    }

    @Override
    public String getCertName() {
        if (getBadge() == null) {
            return null;
        }
        return getBadge().getName();
    }

    @Override
    public String getCertDescription() {
        if (getBadge() == null) {
            return null;
        }
        return getBadge().getDescription();
    }

    @Override
    public String getIssuerId() {
        if (getBadge() == null
                || getBadge().getIssuer() == null
                || getBadge().getIssuer().getId() == null) {
            return null;
        }
        return getBadge().getIssuer().getId().toString();
    }

    @Override
    public String getIssueDate() {
        if (getIssuedOn() == null) {
            return null;
        }
        return getIssuedOn().toString();
    }

    @Override
    public String getUrl() {
        if (getBadge() == null
                || getBadge().getId() == null) {
            return null;
        }
        return getBadge().getId().toString();
    }

    @Override
    public String getRecipientPublicKey() {
        if (getRecipient() == null
                || getRecipient().getRecipientProfile() == null
                || getRecipient().getRecipientProfile().getPublicKey() == null) {
            return null;
        }
        String keyString = getRecipient().getRecipientProfile().getPublicKey().toString();
        if (keyString.startsWith(LMConstants.ECDSA_KOBLITZ_PUBKEY_PREFIX)) {
            keyString = keyString.substring(LMConstants.ECDSA_KOBLITZ_PUBKEY_PREFIX.length());
        }
        return keyString;
    }

    @Override
    public String getSourceId() {
        if (getSignature() == null
                || ListUtils.isEmpty(getSignature().getAnchors())) {
            return null;
        }
        return getSignature().getAnchors().get(0).getSourceId();
    }

    @Override
    public String getMerkleRoot() {
        if (getSignature() == null) {
            return null;
        }
        return getSignature().getMerkleRoot();
    }

    @Override
    public IssuerResponse getIssuer() {
        Issuer issuer = getBadge().getIssuer();
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

package com.learningmachine.android.app.data.cert.v20;

import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.learningmachine.android.app.LMConstants;
import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;
import com.learningmachine.android.app.util.ListUtils;
import com.learningmachine.android.app.util.StringUtils;

import org.joda.time.DateTime;

public class BlockCertV20 extends CertSchemaV20 implements BlockCert {
    private static final String URN_PREFIX = "urn:uuid:";

    private JsonObject mDocumentNode;

    @SerializedName("metadataJson")
    @Expose
    private String mMetadata;

    @SerializedName("displayHtml")
    @Expose
    private String mDisplayHtml;

    public String getDisplayHtml() {
        return mDisplayHtml;
    }

    @Override
    public String getCertUid() {
        if (getBadge() == null
                || getBadge().getId() == null) {
            return null;
        }
        String idString = getBadge().getId()
                .toString();
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
        return getBadge().getIssuer()
                .getId()
                .toString();
    }

    @Override
    public String getIssueDate() {
        if (getIssuedOn() == null) {
            return null;
        }
        return getIssuedOn().toString();
    }

    @Override
    public String getExpirationDate() {
        if (getExpires() == null) {
            return null;
        }
        return getExpires().toString();
    }

    @Override
    public String getUrl() {
        if (StringUtils.isWebUrl(getId())) {
            return getId();
        }
        if (getBadge() == null
                || getBadge().getId() == null) {
            return null;
        }
        return getBadge().getId()
                .toString();
    }

    @Override
    public String getRecipientPublicKey() {
        if (getRecipientProfile() == null
                || getRecipientProfile().getPublicKey() == null) {
            return null;
        }
        String keyString = getRecipientProfile()
                .getPublicKey()
                .toString();
        if (keyString.startsWith(LMConstants.ECDSA_KOBLITZ_PUBKEY_PREFIX)) {
            keyString = keyString.substring(LMConstants.ECDSA_KOBLITZ_PUBKEY_PREFIX.length());
        }
        return keyString;
    }

    @Override
    public String getVerificationPublicKey() {
        if( getVerification() == null || getVerification().getPublicKey() == null) {
            return null;
        }

        String keyString = getVerification().getPublicKey().toString();
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
        return getSignature().getAnchors()
                .get(0)
                .getSourceId();
    }

    @Override
    public String getMerkleRoot() {
        if (getSignature() == null) {
            return null;
        }
        return getSignature().getMerkleRoot();
    }

    @Override
    public String getMetadata() {
        return mMetadata;
    }

    @Override
    public IssuerResponse getIssuer() {
        Issuer issuer = getBadge().getIssuer();
        String name = issuer.getName();
        String email = issuer.getEmail();
        String issuerURL = issuer.getUrl().toString();
        String certUuid = issuer.getId().toString();
        String certsUrl = null;
        String introUrl = null;
        String introducedOn = DateTime.now().toString();
        String imageData = issuer.getImage();
        String analytics = null;

        return new IssuerResponse(name, email, issuerURL, certUuid, certsUrl, introUrl, introducedOn, imageData, analytics);
    }

    @Override
    public JsonObject getDocumentNode() {
        return mDocumentNode;
    }

    @Override
    public String getReceiptHash() {
        return getSignature().getTargetHash();
    }

    public void setDocumentNode(JsonObject documentNode) {
        mDocumentNode = documentNode;
    }


    public Anchor.ChainType getChain() {
        if(getSignature() != null && getSignature().getAnchors() != null) {
            Anchor anchor = getSignature().getAnchors().get(0);
            String anchorChain = anchor.getChain();
            if (anchorChain != null) {
                if (anchorChain.toLowerCase().equals("bitcoinmainnet")) {
                    return Anchor.ChainType.bitcoin;
                } else if (anchorChain.toLowerCase().equals("bitcointestnet")) {
                    return Anchor.ChainType.testnet;
                } else if (anchorChain.toLowerCase().equals("bitcoinregtest")) {
                    return Anchor.ChainType.regtest;
                } else if (anchorChain.toLowerCase().equals("mockchain")) {
                    return Anchor.ChainType.mocknet;
                } else {
                    return Anchor.ChainType.unknown;
                }
            }
        }

        String pubkey = getVerificationPublicKey();
        // Legacy path: we didn't support anything other than testnet and mainnet, so we check the address prefix
        // otherwise try to determine the chain from a bitcoin address
        if (pubkey.startsWith("1") || pubkey.startsWith("ecdsa-koblitz-pubkey:1")) {
            return Anchor.ChainType.bitcoin;
        }

        return Anchor.ChainType.testnet;
    };
}

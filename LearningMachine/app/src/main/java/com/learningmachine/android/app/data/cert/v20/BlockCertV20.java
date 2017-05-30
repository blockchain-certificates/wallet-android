package com.learningmachine.android.app.data.cert.v20;

import com.learningmachine.android.app.LMConstants;
import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.util.ListUtils;

import org.bitcoinj.core.NetworkParameters;
import org.joda.time.DateTime;

public class BlockCertV20 extends CertSchemaV20 implements BlockCert {
    private static final String URN_PREFIX = "urn:uuid:";

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
    public String getAddress(NetworkParameters networkParameters) {
        // FIXME: find it
        return "14X3Xvw6kQA8iA2GZQKo4ZquBLNNamLcpQ";
    }

    @Override
    public IssuerRecord getIssuer() {
        Issuer issuer = getBadge().getIssuer();
        String name = issuer.getName();
        String email = issuer.getEmail();
        String certUuid = issuer.getId().toString();
        String certUrl = null;
        String introUrl = null;
        String introducedOn = DateTime.now().toString();
        IssuerRecord issuerRecord = new IssuerRecord(name, email, certUuid, certUrl, introUrl, introducedOn);
        return issuerRecord;
    }
}

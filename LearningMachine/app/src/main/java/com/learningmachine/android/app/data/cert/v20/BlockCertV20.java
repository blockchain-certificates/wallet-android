package com.learningmachine.android.app.data.cert.v20;

import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.util.ListUtils;

import org.bitcoinj.core.NetworkParameters;
import org.joda.time.DateTime;

public class BlockCertV20 extends CertSchemaV20 implements BlockCert {
    @Override
    public String getCertUid() {
        if (getBadge() == null
                || getBadge().getId() == null) {
            return null;
        }
        return getBadge().getId().toString();
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
        return getRecipient().getRecipientProfile().getPublicKey().toString();
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
        IssuerRecord issuerRecord = new IssuerRecord(issuer.getName(), issuer.getEmail(), issuer.getId().toString(), issuer.getUrl().toString(), issuer.getUrl().toString(), DateTime.now().toString());
        return issuerRecord;
    }
}

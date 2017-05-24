package com.learningmachine.android.app.data.cert.v12;

import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.util.ListUtils;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;

import java.security.SignatureException;

import timber.log.Timber;

public class BlockCertV12 extends BlockchainCertificate implements BlockCert {
    @Override
    public String getCertUid() {
        if (getDocument() == null) {
            return null;
        } else if (getDocument().getAssertion() == null) {
            return null;
        }
        return getDocument().getAssertion().getUid();
    }

    @Override
    public String getCertName() {
        if (getDocument() == null) {
            return null;
        } else if (getDocument().getCertificate() == null) {
            return null;
        }
        return getDocument().getCertificate().getName();
    }

    @Override
    public String getCertDescription() {
        if (getDocument() == null) {
            return null;
        } else if (getDocument().getCertificate() == null) {
            return null;
        }
        return getDocument().getCertificate().getDescription();
    }

    @Override
    public String getIssuerId() {
        if (getDocument() == null) {
            return null;
        } else if (getDocument().getCertificate() == null) {
            return null;
        } else if (getDocument().getCertificate().getIssuer() == null) {
            return null;
        } else if (getDocument().getCertificate().getIssuer().getId() == null) {
            return null;
        }
        return getDocument().getCertificate().getIssuer().getId().toString();
    }

    @Override
    public String getIssueDate() {
        if (getDocument() == null) {
            return null;
        } else if (getDocument().getAssertion() == null) {
            return null;
        }
        return getDocument().getAssertion().getIssuedOn();
    }

    @Override
    public String getUrl() {
        if (getDocument() == null) {
            return null;
        } else if (getDocument().getAssertion() == null) {
            return null;
        } else if (getDocument().getAssertion().getId() == null) {
            return null;
        }
        return getDocument().getAssertion().getId().toString();
    }

    @Override
    public String getRecipientPublicKey() {
        if (getDocument() == null) {
            return null;
        } else if (getDocument().getRecipient() == null) {
            return null;
        }
        return getDocument().getRecipient().getPublicKey();
    }

    @Override
    public String getSourceId() {
        if (getReceipt() == null) {
            return null;
        } else if (ListUtils.isEmpty(getReceipt().getAnchors())) {
            return null;
        }
        return getReceipt().getAnchors().get(0).getSourceId();
    }

    @Override
    public String getMerkleRoot() {
        if (getReceipt() == null) {
            return null;
        }
        return getReceipt().getMerkleRoot();
    }

    @Override
    public String getAddress(NetworkParameters networkParameters) {
        try {
            ECKey ecKey = ECKey.signedMessageToKey(getCertUid(), getDocument().getSignature());
            Address address = ecKey.toAddress(networkParameters);
            return address.toBase58();
        } catch (SignatureException e) {
            Timber.e(e, "The document signature is invalid");
            return null;
        }
    }
}

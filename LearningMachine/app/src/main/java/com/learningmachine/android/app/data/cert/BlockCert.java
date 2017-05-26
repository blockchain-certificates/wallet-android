package com.learningmachine.android.app.data.cert;

import com.learningmachine.android.app.data.model.IssuerRecord;

import org.bitcoinj.core.NetworkParameters;

public interface BlockCert {
    String ECDSA_KOBLITZ_PUBKEY_PREFIX = "ecdsa-koblitz-pubkey:";

    String getCertUid();
    String getCertName();
    String getCertDescription();
    String getIssuerId();
    String getIssueDate();
    String getUrl();
    String getRecipientPublicKey();
    String getSourceId();
    String getMerkleRoot();
    String getAddress(NetworkParameters networkParameters);
    IssuerRecord getIssuer();
}

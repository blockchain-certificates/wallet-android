package com.learningmachine.android.app.data.cert.v12;

import com.learningmachine.android.app.data.cert.BlockCert;

public class BlockCertV12 extends BlockchainCertificate implements BlockCert {
    @Override
    public String getCertUid() {
        return getDocument().getAssertion().getUid();
    }

    @Override
    public String getCertName() {
        return getDocument().getCertificate().getName();
    }

    @Override
    public String getCertDescription() {
        return getDocument().getCertificate().getDescription();
    }

    @Override
    public String getIssuerId() {
        return getDocument().getCertificate().getIssuer().getId().toString();
    }

    @Override
    public String getIssueDate() {
        return getDocument().getAssertion().getIssuedOn();
    }

    @Override
    public String getUrl() {
        return getDocument().getAssertion().getId().toString();
    }

    @Override
    public String getRecipientPublicKey() {
        return getDocument().getRecipient().getPublicKey();
    }
}

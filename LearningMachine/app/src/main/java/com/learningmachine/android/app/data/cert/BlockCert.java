package com.learningmachine.android.app.data.cert;

import com.learningmachine.android.app.data.webservice.response.IssuerResponse;

import org.bitcoinj.core.NetworkParameters;

public interface BlockCert {


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
    IssuerResponse getIssuer();
}

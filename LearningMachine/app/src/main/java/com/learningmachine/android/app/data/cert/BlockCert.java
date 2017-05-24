package com.learningmachine.android.app.data.cert;

public interface BlockCert {
    String getCertUid();
    String getCertName();
    String getCertDescription();
    String getIssuerId();
    String getIssueDate();
    String getUrl();
    String getRecipientPublicKey();
}

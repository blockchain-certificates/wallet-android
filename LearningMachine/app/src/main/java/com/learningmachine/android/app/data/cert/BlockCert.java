package com.learningmachine.android.app.data.cert;

import com.google.gson.JsonObject;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;

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
    JsonObject getCanonicalizedJson();
    void setCanonicalizedJson(JsonObject canonicalizedJson);
    IssuerResponse getIssuer();
}

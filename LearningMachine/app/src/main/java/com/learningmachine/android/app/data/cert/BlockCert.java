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
    String getVerificationPublicKey();
    String getSourceId();
    String getMerkleRoot();
    String getMetadata();
    String getReceiptHash();
    String getExpirationDate();

    /**
     * @return The portion of the certificate whose hash needs to be compared against the
     *         hash stored in the blockchain transaction
     */
    JsonObject getDocumentNode();

    /**
     * Different versions of the Blockchain certificates use different rules for verifying
     * the integrity of the certificate. In v1.2, the certificate.document's hash is checked
     * against the hash in the blockchain transaction. In v2.0, the hash is calculated on the
     * entire certificate sans its certificate.signature.
     *
     * @param documentNode The portion of the certificate that is used to calculate the hash
     */
    void setDocumentNode(JsonObject documentNode);

    IssuerResponse getIssuer();
}

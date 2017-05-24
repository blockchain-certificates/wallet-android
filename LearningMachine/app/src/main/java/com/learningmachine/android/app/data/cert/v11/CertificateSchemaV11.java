
package com.learningmachine.android.app.data.cert.v11;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Certificate schema V1.1
 * <p>
 * 
 * 
 */
public class CertificateSchemaV11 {

    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("certificate")
    @Expose
    private Certificate certificate;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("assertion")
    @Expose
    private Assertion assertion;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("verify")
    @Expose
    private Verify verify;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("recipient")
    @Expose
    private Recipient recipient;
    /**
     * String of signature created when the Bitcoin private key signs the value in the attribute-signed field.
     * 
     */
    @SerializedName("signature")
    @Expose
    private String signature;
    /**
     * Extension object that includes extra fields not in the standard.
     * 
     */
    @SerializedName("extension")
    @Expose
    private Extension extension;

    /**
     * 
     * (Required)
     * 
     */
    public Certificate getCertificate() {
        return certificate;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    /**
     * 
     * (Required)
     * 
     */
    public Assertion getAssertion() {
        return assertion;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setAssertion(Assertion assertion) {
        this.assertion = assertion;
    }

    /**
     * 
     * (Required)
     * 
     */
    public Verify getVerify() {
        return verify;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setVerify(Verify verify) {
        this.verify = verify;
    }

    /**
     * 
     * (Required)
     * 
     */
    public Recipient getRecipient() {
        return recipient;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }

    /**
     * String of signature created when the Bitcoin private key signs the value in the attribute-signed field.
     * 
     */
    public String getSignature() {
        return signature;
    }

    /**
     * String of signature created when the Bitcoin private key signs the value in the attribute-signed field.
     * 
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }

    /**
     * Extension object that includes extra fields not in the standard.
     * 
     */
    public Extension getExtension() {
        return extension;
    }

    /**
     * Extension object that includes extra fields not in the standard.
     * 
     */
    public void setExtension(Extension extension) {
        this.extension = extension;
    }

}

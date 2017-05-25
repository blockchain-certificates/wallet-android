
package com.learningmachine.android.app.data.cert.v11;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Extension object that includes extra fields not in the standard.
 * 
 */
public class Extension {

    @SerializedName("assertion")
    @Expose
    private Assertion assertion;
    @SerializedName("verify")
    @Expose
    private Verify verify;
    @SerializedName("certificate")
    @Expose
    private Certificate certificate;
    @SerializedName("recipient")
    @Expose
    private Recipient recipient;

    public Assertion getAssertion() {
        return assertion;
    }

    public void setAssertion(Assertion assertion) {
        this.assertion = assertion;
    }

    public Verify getVerify() {
        return verify;
    }

    public void setVerify(Verify verify) {
        this.verify = verify;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    public Recipient getRecipient() {
        return recipient;
    }

    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }

}

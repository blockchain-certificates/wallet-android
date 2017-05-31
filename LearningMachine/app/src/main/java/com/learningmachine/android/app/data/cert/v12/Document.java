
package com.learningmachine.android.app.data.cert.v12;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Certificate Document Version 1.2 Schema
 * <p>
 * The complete certificate document, including the assertion, certificate, and issuer. Doesn't include the blockchain receipt. This part is hashed and placed on the blockchain
 * 
 */
class Document {

    /**
     * A link to a valid JSON-LD context file, that maps term names to contexts. Blockchain Certificate contexts may also define JSON-schema to validate Blockchain Certificates against. In a Blockchain Certificate Object, this will almost always be a string:uri to a single context file, but might rarely be an array of links or context objects instead. This schema also allows direct mapping of terms to IRIs by using an object as an option within an array.
     * 
     */
    @SerializedName("@context")
    @Expose
    private Object context;
    /**
     * A type or an array of types that the Blockchain Certificate object represents. The first or only item should be 'CertificateDocument', and any others should each be an IRI (usually a URL) corresponding to a definition of the type itself. In almost all cases, there will be only one type: 'CertificateDocument'
     * (Required)
     * 
     */
    @SerializedName("type")
    @Expose
    private Object type;
    /**
     * Blockchain Certificates Certificate Schema, Version 1.2
     * <p>
     * Extends the Open Badges Certificate Schema for certificates on the blockchain
     * (Required)
     * 
     */
    @SerializedName("certificate")
    @Expose
    private Certificate certificate;
    /**
     * Blockchain Certificates Assertion Schema, Version 1.2
     * <p>
     * Extends the Open Badges Assertion Schema for certificates on the blockchain
     * (Required)
     * 
     */
    @SerializedName("assertion")
    @Expose
    private Assertion assertion;
    /**
     * V1.2 notice: the Blockchain Certificates VerificationObject will change in the next schema version to be consistent with OBI VerificationObjects. This work is in progress.
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

    @SerializedName("signature")
    @Expose
    private String signature;

    /**
     * A link to a valid JSON-LD context file, that maps term names to contexts. Blockchain Certificate contexts may also define JSON-schema to validate Blockchain Certificates against. In a Blockchain Certificate Object, this will almost always be a string:uri to a single context file, but might rarely be an array of links or context objects instead. This schema also allows direct mapping of terms to IRIs by using an object as an option within an array.
     * 
     */
    public Object getContext() {
        return context;
    }

    /**
     * A link to a valid JSON-LD context file, that maps term names to contexts. Blockchain Certificate contexts may also define JSON-schema to validate Blockchain Certificates against. In a Blockchain Certificate Object, this will almost always be a string:uri to a single context file, but might rarely be an array of links or context objects instead. This schema also allows direct mapping of terms to IRIs by using an object as an option within an array.
     * 
     */
    public void setContext(Object context) {
        this.context = context;
    }

    /**
     * A type or an array of types that the Blockchain Certificate object represents. The first or only item should be 'CertificateDocument', and any others should each be an IRI (usually a URL) corresponding to a definition of the type itself. In almost all cases, there will be only one type: 'CertificateDocument'
     * (Required)
     * 
     */
    public Object getType() {
        return type;
    }

    /**
     * A type or an array of types that the Blockchain Certificate object represents. The first or only item should be 'CertificateDocument', and any others should each be an IRI (usually a URL) corresponding to a definition of the type itself. In almost all cases, there will be only one type: 'CertificateDocument'
     * (Required)
     * 
     */
    public void setType(Object type) {
        this.type = type;
    }

    /**
     * Blockchain Certificates Certificate Schema, Version 1.2
     * <p>
     * Extends the Open Badges Certificate Schema for certificates on the blockchain
     * (Required)
     * 
     */
    public Certificate getCertificate() {
        return certificate;
    }

    /**
     * Blockchain Certificates Certificate Schema, Version 1.2
     * <p>
     * Extends the Open Badges Certificate Schema for certificates on the blockchain
     * (Required)
     * 
     */
    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    /**
     * Blockchain Certificates Assertion Schema, Version 1.2
     * <p>
     * Extends the Open Badges Assertion Schema for certificates on the blockchain
     * (Required)
     * 
     */
    public Assertion getAssertion() {
        return assertion;
    }

    /**
     * Blockchain Certificates Assertion Schema, Version 1.2
     * <p>
     * Extends the Open Badges Assertion Schema for certificates on the blockchain
     * (Required)
     * 
     */
    public void setAssertion(Assertion assertion) {
        this.assertion = assertion;
    }

    /**
     * V1.2 notice: the Blockchain Certificates VerificationObject will change in the next schema version to be consistent with OBI VerificationObjects. This work is in progress.
     * (Required)
     * 
     */
    public Verify getVerify() {
        return verify;
    }

    /**
     * V1.2 notice: the Blockchain Certificates VerificationObject will change in the next schema version to be consistent with OBI VerificationObjects. This work is in progress.
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

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}

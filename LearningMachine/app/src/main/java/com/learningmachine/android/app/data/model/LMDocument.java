package com.learningmachine.android.app.data.model;

import com.google.gson.annotations.SerializedName;

public class LMDocument {
    @SerializedName("signature")
    private String mSignature;
    @SerializedName("recipient")
    private Recipient mRecipient;
    @SerializedName("@context")
    private String mDocumentResponseContext;
    @SerializedName("type")
    private String mType;
    @SerializedName("assertion")
    private LMAssertion mLMAssertion;
    @SerializedName("certificate")
    private Certificate mCertificate;
    @SerializedName("verify")
    private Verify mVerify;

    public String getSignature() {
        return mSignature;
    }

    public void setSignature(String signature) {
        mSignature = signature;
    }

    public Recipient getRecipient() {
        return mRecipient;
    }

    public void setRecipient(Recipient recipient) {
        mRecipient = recipient;
    }

    public String getDocumentResponseContext() {
        return mDocumentResponseContext;
    }

    public void setDocumentResponseContext(String documentResponseContext) {
        mDocumentResponseContext = documentResponseContext;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public LMAssertion getLMAssertion() {
        return mLMAssertion;
    }

    public void setLMAssertion(LMAssertion LMAssertion) {
        mLMAssertion = LMAssertion;
    }

    public Certificate getCertificate() {
        return mCertificate;
    }

    public void setCertificate(Certificate certificate) {
        mCertificate = certificate;
    }

    public Verify getVerify() {
        return mVerify;
    }

    public void setVerify(Verify verify) {
        mVerify = verify;
    }
}

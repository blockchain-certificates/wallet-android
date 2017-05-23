package com.learningmachine.android.app.data.model;

import com.google.gson.annotations.SerializedName;

public class Document {
    @SerializedName("@context")
    private String mContext;
    @SerializedName("assertion")
    private LMAssertion mAssertion;
    @SerializedName("signature")
    private String mSignature;
    @SerializedName("certificate")
    private InnerCertificate mInnerCertificate;

    public LMAssertion getAssertion() {
        return mAssertion;
    }

    public String getSignature() {
        return mSignature;
    }

    public InnerCertificate getInnerCertificate() {
        return mInnerCertificate;
    }

    static class InnerCertificate {
        @SerializedName("description")
        private String mDescription;
        @SerializedName("issuer")
        private Issuer mIssuer;

        public String getIssuerUuid() {
            return mIssuer != null ? mIssuer.getUuid() : null;
        }
    }
}

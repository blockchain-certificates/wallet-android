package com.learningmachine.android.app.data.webservice.request;

import com.google.gson.annotations.SerializedName;

public class IssuerIntroductionPayloadRequest {
    @SerializedName("bitcoinAddress") private String mBitcoinAddress;
    @SerializedName("nonce") private String mNonce;

    public IssuerIntroductionPayloadRequest(String bitcoinAddress, String nonce) {
        mBitcoinAddress = bitcoinAddress;
        mNonce = nonce;
    }

    public void setBitcoinAddress(String bitcoinAddress) {
        mBitcoinAddress = bitcoinAddress;
    }

    public String getBitcoinAddress() {
        return mBitcoinAddress;
    }

    public void setNonce(String nonce) {
        mNonce = nonce;
    }

    public String getNonce() {
        return mNonce;
    }

}

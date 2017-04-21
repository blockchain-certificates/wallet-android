package com.learningmachine.android.app.data.webservice.request;

import com.google.gson.annotations.SerializedName;

public class IssuerIntroductionPayload {
    @SerializedName("bitcoinAddress")
    private String mBitcoinAddress;
    @SerializedName("nonce")
    private String mNonce;

    public IssuerIntroductionPayload(String bitcoinAddress, String nonce) {
        mBitcoinAddress = bitcoinAddress;
        mNonce = nonce;
    }

    public void setBitcoinAddress(String bitcoinAddress){
        this.mBitcoinAddress = bitcoinAddress;
    }

    public String getBitcoinAddress(){
        return mBitcoinAddress;
    }

    public void setNonce(String nonce){
        this.mNonce = nonce;
    }

    public String getNonce(){
        return mNonce;
    }

}

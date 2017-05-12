package com.learningmachine.android.app.data.model;

import com.google.gson.annotations.SerializedName;

public class TxRecordOut {
    @SerializedName("spent")
    private boolean mSpent;
    @SerializedName("tx_index")
    private int mTxIndex;
    @SerializedName("type")
    private int mType;
    @SerializedName("addr")
    private String mAddress;
    @SerializedName("value")
    private int mValue;
    @SerializedName("n")
    private int mSequenceNumber; // TODO: confirm what this is
    @SerializedName("script")
    private String mScript;

    public int getValue() {
        return mValue;
    }

    public String getScript() {
        return mScript;
    }

    public boolean isSpent() {
        return mSpent;
    }

    public String getAddress() {
        return mAddress;
    }
}

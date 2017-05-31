package com.learningmachine.android.app.data.model;

import com.google.gson.annotations.SerializedName;

class TxRecordInput {
    @SerializedName("sequence")
    private long mSequence;
    @SerializedName("prev_out")
    private TxRecordOut mPreviousOut;
    @SerializedName("script")
    private String mScript;

    public TxRecordOut getPreviousOut() {
        return mPreviousOut;
    }
}

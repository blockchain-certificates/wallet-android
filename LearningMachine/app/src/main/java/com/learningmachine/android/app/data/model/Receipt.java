package com.learningmachine.android.app.data.model;

import com.google.gson.annotations.SerializedName;
import com.learningmachine.android.app.util.ListUtils;

import java.util.List;

public class Receipt {

    @SerializedName("anchors")
    private List<Anchor> mAnchorList;
    @SerializedName("@context")
    private String mReceiptResponseContext;
    @SerializedName("type")
    private String mType;
    @SerializedName("proof")
    private List<Proof> mProofList;
    @SerializedName("merkleRoot")
    private String mMerkleRoot;
    @SerializedName("targetHash")
    private String mTargetHash;

    public String getFirstAnchorSourceId() {
        return ListUtils.isEmpty(mAnchorList) ? null : mAnchorList.get(0).getSourceId();
    }

    public String getMerkleRoot() {
        return mMerkleRoot;
    }
}

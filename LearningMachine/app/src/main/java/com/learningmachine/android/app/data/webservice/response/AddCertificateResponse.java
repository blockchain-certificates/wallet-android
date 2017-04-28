package com.learningmachine.android.app.data.webservice.response;

import com.google.gson.annotations.SerializedName;
import com.learningmachine.android.app.data.model.LMDocument;
import com.learningmachine.android.app.data.model.Receipt;

public class AddCertificateResponse {

    @SerializedName("document")
    private LMDocument mDocument;
    @SerializedName("@context")
    private String mAddCertResponseContext;
    @SerializedName("type")
    private String mType;
    @SerializedName("receipt")
    private Receipt mReceipt;

    public LMDocument getDocument() {
        return mDocument;
    }

    public String getAddCertResponseContext() {
        return mAddCertResponseContext;
    }

    public String getType() {
        return mType;
    }

    public Receipt getReceipt() {
        return mReceipt;
    }
}

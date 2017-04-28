package com.learningmachine.android.app.data.webservice.response;

import com.google.gson.annotations.SerializedName;
import com.learningmachine.android.app.data.model.Issuer;

public class IssuerResponse extends Issuer {

    @SerializedName("image")
    private String mImageData;

    public IssuerResponse(String name, String email, String uuid, String certsUrl, String introUrl) {
        super(name, email, uuid, certsUrl, introUrl);
    }

    public String getImageData() {
        return mImageData;
    }
}

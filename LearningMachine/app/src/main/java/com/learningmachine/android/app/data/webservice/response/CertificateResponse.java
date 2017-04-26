package com.learningmachine.android.app.data.webservice.response;

import com.google.gson.annotations.SerializedName;
import com.learningmachine.android.app.data.model.Certificate;

public class CertificateResponse extends Certificate {

    @SerializedName("image")
    private String mImageData;

    public CertificateResponse(String name, String description) {
        super(name, description);
    }

    public String getImageData() {
        return mImageData;
    }
}

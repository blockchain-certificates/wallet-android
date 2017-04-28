package com.learningmachine.android.app.data.webservice.response;

import com.google.gson.annotations.SerializedName;
import com.learningmachine.android.app.data.model.Certificate;

public class CertificateResponse extends Certificate {

    @SerializedName("image")
    private String mImageData;

    public CertificateResponse(String uuid, String issuerUuid, String name, String description) {
        super(uuid, issuerUuid, name, description);
    }

    public String getImageData() {
        return mImageData;
    }
}

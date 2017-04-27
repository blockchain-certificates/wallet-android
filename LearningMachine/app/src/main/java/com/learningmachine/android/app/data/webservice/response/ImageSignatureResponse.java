package com.learningmachine.android.app.data.webservice.response;

import com.google.gson.annotations.SerializedName;
import com.learningmachine.android.app.data.model.ImageSignature;

public class ImageSignatureResponse extends ImageSignature {

    @SerializedName("image")
    private String mImageData;

    public String getImageData() {
        return mImageData;
    }
}

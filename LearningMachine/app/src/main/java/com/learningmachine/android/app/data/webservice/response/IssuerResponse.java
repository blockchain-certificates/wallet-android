package com.learningmachine.android.app.data.webservice.response;

import com.google.gson.annotations.SerializedName;
import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.util.ListUtils;

public class IssuerResponse extends IssuerRecord {

    @SerializedName("image")
    private String mImageData;

    public IssuerResponse(String name, String email, String uuid, String certsUrl, String introUrl, String introducedOn) {
        super(name, email, uuid, certsUrl, introUrl, introducedOn);
    }

    public String getImageData() {
        return mImageData;
    }

    public boolean verifyAddress(String address) {
        if (ListUtils.isEmpty(getIssuerKeys())) {
            return false;
        }
        return getIssuerKeys().get(0).verifyAddress(address);
    }
}

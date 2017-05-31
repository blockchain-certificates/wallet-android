package com.learningmachine.android.app.data.webservice.response;

import com.google.gson.annotations.SerializedName;
import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.data.model.KeyRotation;
import com.learningmachine.android.app.data.model.TxRecord;
import com.learningmachine.android.app.util.ListUtils;

import java.util.List;

public class IssuerResponse extends IssuerRecord {

    @SerializedName("image")
    private String mImageData;

    public IssuerResponse(String name, String email, String uuid, String certsUrl, String introUrl, String introducedOn, String imageData) {
        super(name, email, uuid, certsUrl, introUrl, introducedOn);
        mImageData = imageData;
    }

    public String getImageData() {
        return mImageData;
    }

    public boolean verifyTransaction(TxRecord txRecord) {
        List<KeyRotation> issuerKeys = getIssuerKeys();
        if (ListUtils.isEmpty(issuerKeys)) {
            return false;
        }
        for (KeyRotation issuerKey : issuerKeys) {
            if (issuerKey.verifyTransaction(txRecord)) {
                return true;
            }
        }
        return false;
    }
}

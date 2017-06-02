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
    @SerializedName("introductionAuthenticationMethod")
    private String mIntroductionMethod;
    @SerializedName("introductionSuccessURL")
    private String mIntroductionSuccessUrlString;
    @SerializedName("introductionErrorURL")
    private String mIntroductionErrorUrlString;

    public IssuerResponse(String name, String email, String uuid, String certsUrl, String introUrl, String introducedOn, String imageData, String analyticsUrlString) {
        super(name, email, uuid, certsUrl, introUrl, introducedOn, analyticsUrlString);
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
        /*
         * Only one public key is active at any given time. The keys may expire or be revoked.
         * Need to find the one that is active and covers the period of the `txRecord` to validate.
         * Thus, need to go over all of them and find a matching one.
         */
        for (KeyRotation issuerKey : issuerKeys) {
            if (issuerKey.verifyTransaction(txRecord)) {
                return true;
            }
        }
        return false;
    }
}

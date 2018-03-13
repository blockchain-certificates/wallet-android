package com.learningmachine.android.app.data.webservice.response;

import com.google.gson.annotations.SerializedName;
import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.data.model.KeyRotation;
import com.learningmachine.android.app.data.model.TxRecord;
import com.learningmachine.android.app.util.ListUtils;

import java.util.List;

public class IssuerResponse extends IssuerRecord {
    private static final String WEB_AUTH_METHOD = "web";

    @SerializedName("image")
    private String mImageData;
    @SerializedName("introductionAuthenticationMethod")
    private String mIntroductionMethod;
    @SerializedName("introductionSuccessURL")
    private String mIntroductionSuccessUrlString;
    @SerializedName("introductionErrorURL")
    private String mIntroductionErrorUrlString;

    public IssuerResponse(String name, String email, String issuerURL, String uuid, String certsUrl, String introUrl, String introducedOn, String imageData, String analyticsUrlString) {
        super(name, email, issuerURL, uuid, certsUrl, introUrl, introducedOn, analyticsUrlString, null);
        mImageData = imageData;
    }

    public String getImageData() {
        return mImageData;
    }

    public KeyRotation.KeyStatus verifyTransaction(TxRecord txRecord) {
        List<KeyRotation> issuerKeys = getIssuerKeys();
        if (ListUtils.isEmpty(issuerKeys)) {
            return KeyRotation.KeyStatus.KEY_INVALID;
        }
        /*
         * Only one public key is active at any given time. The keys may expire or be revoked.
         * Need to find the one that is active and covers the period of the `txRecord` to validate.
         * Thus, need to go over all of them and find a matching one.
         */
        KeyRotation.KeyStatus status = KeyRotation.KeyStatus.KEY_INVALID;
        for (KeyRotation issuerKey : issuerKeys) {
            KeyRotation.KeyStatus thisKeyStatus = issuerKey.verifyTransaction(txRecord);
            if (thisKeyStatus == KeyRotation.KeyStatus.KEY_VALID) {
                return KeyRotation.KeyStatus.KEY_VALID;
            }

            // Revoked > Expired > Invalid
            if(thisKeyStatus == KeyRotation.KeyStatus.KEY_REVOKED &&
                    (status == KeyRotation.KeyStatus.KEY_INVALID || status == KeyRotation.KeyStatus.KEY_EXPIRED)){
                status = KeyRotation.KeyStatus.KEY_REVOKED;
            }

            if(thisKeyStatus == KeyRotation.KeyStatus.KEY_EXPIRED &&
                    status == KeyRotation.KeyStatus.KEY_INVALID){
                status = KeyRotation.KeyStatus.KEY_EXPIRED;
            }

        }
        return status;
    }

    public boolean usesWebAuth() {
        return WEB_AUTH_METHOD.equals(mIntroductionMethod);
    }

    public String getIntroductionSuccessUrlString() {
        return mIntroductionSuccessUrlString;
    }

    public String getIntroductionErrorUrlString() {
        return mIntroductionErrorUrlString;
    }
}

package com.learningmachine.android.app.data.model;

import com.google.gson.annotations.SerializedName;
import com.learningmachine.android.app.util.DateUtils;
import com.learningmachine.android.app.util.ImageUtils;
import com.learningmachine.android.app.util.ListUtils;

import java.io.Serializable;
import java.util.List;

public class Issuer implements Serializable {

    /** The name of the issuer. */
    @SerializedName("name")
    private String mName;

    /** The email address where you can contact the issuer */
    @SerializedName("email")
    private String mEmail;

    /** Unique identifier for an Issuer. Also, the URL where you can re-request data.
     * This is useful if an instance of this struct only has partial data, or if you want to see that the keys are still valid. */
    @SerializedName("id")
    private String mUuid;

    /** Where you can go to check a list of certificates issued by this issuer. */
    @SerializedName("url")
    private String mCertsUrl;

    /** The URL where you can make a POST request with recipient data in order to introduce a Recipient to an Issuer.
     * For more information, look at `IssuerIntroductionRequest` */
    @SerializedName("introductionURL")
    private String mIntroUrl;

    /** An ordered list of KeyRotation objects, with the most recent key rotation first.
     * These represent the keys used to issue certificates during specific date ranges */
    @SerializedName("issuerKeys")
    private List<KeyRotation> mIssuerKeys;

    /** An ordered list of KeyRotation objects, with the most recent key rotation first.
     * These represent the keys used to revoke certificates. */
    @SerializedName("revocationKeys")
    private List<KeyRotation> mRevocationKeys;


    public Issuer(String name, String email, String uuid, String certsUrl, String introUrl) {
        mName = name;
        mEmail = email;
        mUuid = uuid;
        mCertsUrl = certsUrl;
        mIntroUrl = introUrl;
    }

    public String getName() {
        return mName;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getUuid() {
        return mUuid;
    }

    public String getCertsUrl() {
        return mCertsUrl;
    }

    public String getIntroUrl() {
        return mIntroUrl;
    }

    public List<KeyRotation> getIssuerKeys() {
        return mIssuerKeys;
    }

    public void setIssuerKeys(List<KeyRotation> issuerKeys) {
        mIssuerKeys = issuerKeys;
    }

    public List<KeyRotation> getRevocationKeys() {
        return mRevocationKeys;
    }

    public void setRevocationKeys(List<KeyRotation> revocationKeys) {
        mRevocationKeys = revocationKeys;
    }


    /** Image filename, created with the md5 of mUuid*/
    public String getImageFilename() {
        return ImageUtils.getImageFilename(mUuid);
    }

    /** A convenience method for the most recent (and theoretically only valid) issuerKey. */
    public KeyRotation getPublicKey() {
        if (!ListUtils.isEmpty(mIssuerKeys)) {
            return mIssuerKeys.get(0);
        }
        return null;
    }

    public String getIntroducedDateString() {
        KeyRotation keyRotation = getPublicKey();
        if (keyRotation == null) {
            return null;
        }
        String dateString = keyRotation.getCreatedDate();
        return DateUtils.formatDateString(dateString);
    }
}

package com.learningmachine.android.app.data.model;

import com.google.gson.annotations.SerializedName;
import com.learningmachine.android.app.LMConstants;
import com.learningmachine.android.app.util.ImageUtils;
import com.learningmachine.android.app.util.ListUtils;

import java.util.List;

import timber.log.Timber;

public class IssuerRecord {

    /** The name of the issuer. */
    @SerializedName("name")
    private String mName;

    /** The email address where you can contact the issuer */
    @SerializedName("email")
    private String mEmail;

    @SerializedName("issuerURL")
    private String mIssuerURL;

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
    @SerializedName(value = "publicKey", alternate = {"publicKeys"})
    private List<KeyRotation> mIssuerKeys;

    /** An ordered list of KeyRotation objects, with the most recent key rotation first.
     * These represent the keys used to revoke certificates. */
    @SerializedName("revocationKeys")
    private List<KeyRotation> mRevocationKeys;


    /** A URL pointing to a list of certificate urls that have been revoked by the issuer */
    @SerializedName("revocationList")
    private String mRevocationList;

    @SerializedName("analyticsURL")
    private String mAnalyticsUrlString;

    // created when added to DB
    private String mIntroducedOn;

    private String mRecipientPubKey;

    public IssuerRecord(String name, String email, String issuerURL, String uuid, String certsUrl, String introUrl, String introducedOn, String analyticsUrlString, String recipientPubKey) {
        mName = name;
        mEmail = email;
        mIssuerURL = issuerURL;
        mUuid = uuid;
        mCertsUrl = certsUrl;
        mIntroUrl = introUrl;
        mIntroducedOn = introducedOn;
        mAnalyticsUrlString = analyticsUrlString;
        mRecipientPubKey = recipientPubKey;
    }

    public String getName() {
        return mName;
    }

    public String getIssuerURL() {
        return mIssuerURL;
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
        if (mIntroUrl == null) {
            return "";
        }
        return mIntroUrl;
    }

    public List<KeyRotation> getIssuerKeys() {
        return mIssuerKeys;
    }

    public void setIssuerKeys(List<KeyRotation> issuerKeys) {
        mIssuerKeys = issuerKeys;
    }

    public String getRevocationList() {
        return mRevocationList;
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

    public String getPublicKeyAddress() {
        try {
            KeyRotation publicKey = getPublicKey();
            String key = publicKey.getKey();
            if (key.startsWith(LMConstants.ECDSA_KOBLITZ_PUBKEY_PREFIX)) {
                key = key.substring(LMConstants.ECDSA_KOBLITZ_PUBKEY_PREFIX.length());
            }
            return key;
        } catch (NullPointerException e) {
            Timber.e(e, "Unable to retrieve public key address");
            return null;
        }
    }

    public String getIntroducedOn() {
        return mIntroducedOn;
    }

    public void setIntroducedOn(String introducedOn) {
        mIntroducedOn = introducedOn;
    }

    public String getAnalyticsUrlString() {
        return mAnalyticsUrlString;
    }

    public String getRecipientPubKey() {
        return mRecipientPubKey;
    }

    public void setRecipientPubKey(String recipientPubKey) {
        this.mRecipientPubKey = recipientPubKey;
    }

    // Note: Below are convenience variables to store information about an issuer which is
    // generally expensive to calculate
    public int cachedNumberOfCertificatesForIssuer = -1;

    @Override
    public String toString() {
        return mIntroUrl;
    }
}

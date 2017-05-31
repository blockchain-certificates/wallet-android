package com.learningmachine.android.app.data.model;

import com.google.gson.annotations.SerializedName;
import com.learningmachine.android.app.LMConstants;
import com.learningmachine.android.app.data.cert.BlockCert;

import org.joda.time.DateTime;

import java.io.Serializable;

public class KeyRotation implements Serializable {

    @SerializedName("created")
    private String mCreatedDate;
    @SerializedName("expires")
    private String mExpiresDate;
    @SerializedName("revoked")
    private String mRevokedDate;
    @SerializedName("publicKey")
    private String mKey;

    public KeyRotation(String createdDate, String key) {
        mCreatedDate = createdDate;
        mKey = key;
    }

    public String getCreatedDate() {
        return mCreatedDate;
    }

    public DateTime getCreatedDateTime() {
        return DateTime.parse(mCreatedDate);
    }

    public String getKey() {
        return mKey;
    }

    public String getExpiresDate() {
        return mExpiresDate;
    }

    public String getRevokedDate() {
        return mRevokedDate;
    }

    public boolean verifyAddress(String address) {
        String keyString = getKey();
        if (address == null || keyString == null) {
            return false;
        }
        // normalize the key string
        // TODO: abstract away from the specific key format
        if (keyString.startsWith(LMConstants.ECDSA_KOBLITZ_PUBKEY_PREFIX)) {
            keyString = keyString.substring(LMConstants.ECDSA_KOBLITZ_PUBKEY_PREFIX.length());
        }
        // TODO: check expiration and revocation
        return address.equals(keyString);
    }
}

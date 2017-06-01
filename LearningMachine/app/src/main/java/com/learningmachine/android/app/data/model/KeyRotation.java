package com.learningmachine.android.app.data.model;

import com.google.gson.annotations.SerializedName;
import com.learningmachine.android.app.LMConstants;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class KeyRotation {

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

    public boolean verifyTransaction(TxRecord txRecord) {
        TxRecordOut previousOut = txRecord.getInputsPreviousOut();
        if (previousOut == null) {
            return false;
        }
        String address = previousOut.getAddress();
        String keyString = getKey();
        if (address == null || keyString == null) {
            return false;
        }
        // normalize the key string
        // TODO: abstract away from the specific key format
        if (keyString.startsWith(LMConstants.ECDSA_KOBLITZ_PUBKEY_PREFIX)) {
            keyString = keyString.substring(LMConstants.ECDSA_KOBLITZ_PUBKEY_PREFIX.length());
        }
        if (!address.equals(keyString)) {
            return false;
        }

        // check validity dates
        DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime();
        DateTime createdDate = dateTimeFormatter.parseDateTime(mCreatedDate);
        DateTime txRecordTimestamp = txRecord.getDateTime();
        if (createdDate.isAfter(txRecordTimestamp)) {
            return false;
        }
        if (mExpiresDate != null) {
            DateTime expiresDate = dateTimeFormatter.parseDateTime(mExpiresDate);
            if (expiresDate.isBefore(txRecordTimestamp)) {
                return false;
            }
        }
        if (mRevokedDate != null) {
            DateTime revokedDate = dateTimeFormatter.parseDateTime(mRevokedDate);
            if (revokedDate.isBefore(txRecordTimestamp)) {
                return false;
            }
        }
        return true;
    }
}

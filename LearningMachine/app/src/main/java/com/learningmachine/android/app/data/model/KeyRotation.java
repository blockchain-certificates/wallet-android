package com.learningmachine.android.app.data.model;

import com.google.gson.annotations.SerializedName;
import com.learningmachine.android.app.LMConstants;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class KeyRotation {

    public enum KeyStatus {
        KEY_VALID,
        KEY_EXPIRED,
        KEY_REVOKED,
        KEY_INVALID
    }

    @SerializedName("created")
    private String mCreatedDate;
    @SerializedName("expires")
    private String mExpiresDate;
    @SerializedName("revoked")
    private String mRevokedDate;
    @SerializedName(value = "id", alternate = {"publicKey"})
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

    public KeyStatus verifyTransaction(TxRecord txRecord) {
        TxRecordOut previousOut = txRecord.getInputsPreviousOut();
        if (previousOut == null) {
            return KeyStatus.KEY_INVALID;
        }
        String address = previousOut.getAddress();
        String keyString = getKey();
        if (address == null || keyString == null) {
            return KeyStatus.KEY_INVALID;
        }
        // normalize the key string
        // TODO: abstract away from the specific key format
        if (keyString.startsWith(LMConstants.ECDSA_KOBLITZ_PUBKEY_PREFIX)) {
            keyString = keyString.substring(LMConstants.ECDSA_KOBLITZ_PUBKEY_PREFIX.length());
        }
        if (!address.equals(keyString)) {
            return KeyStatus.KEY_INVALID;
        }

        // check validity dates
        DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime();
        DateTime createdDate = dateTimeFormatter.parseDateTime(mCreatedDate);
        DateTime txRecordTimestamp = txRecord.getDateTime();
        if (createdDate.isAfter(txRecordTimestamp)) {
            return KeyStatus.KEY_INVALID;
        }
        if (mExpiresDate != null) {
            DateTime expiresDate = dateTimeFormatter.parseDateTime(mExpiresDate);
            if (expiresDate.isBefore(txRecordTimestamp)) {
                return KeyStatus.KEY_EXPIRED;
            }
        }
        if (mRevokedDate != null) {
            DateTime revokedDate = dateTimeFormatter.parseDateTime(mRevokedDate);
            if (revokedDate.isBefore(txRecordTimestamp)) {
                return KeyStatus.KEY_REVOKED;
            }
        }
        return KeyStatus.KEY_VALID;
    }
}

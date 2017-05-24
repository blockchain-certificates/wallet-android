
package com.learningmachine.android.app.data.cert.v11;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.joda.time.DateTime;

public class IssuerKey {

    /**
     * Date time ISO-8601 format of the date that the keys were issued.
     * (Required)
     * 
     */
    @SerializedName("date")
    @Expose
    private DateTime date;
    /**
     * Bitcoin address (compressed public key, usually 24 characters) that the issuer uses to issue the certificates.
     * (Required)
     * 
     */
    @SerializedName("key")
    @Expose
    private String key;

    /**
     * Date time ISO-8601 format of the date that the keys were issued.
     * (Required)
     * 
     */
    public DateTime getDate() {
        return date;
    }

    /**
     * Date time ISO-8601 format of the date that the keys were issued.
     * (Required)
     * 
     */
    public void setDate(DateTime date) {
        this.date = date;
    }

    /**
     * Bitcoin address (compressed public key, usually 24 characters) that the issuer uses to issue the certificates.
     * (Required)
     * 
     */
    public String getKey() {
        return key;
    }

    /**
     * Bitcoin address (compressed public key, usually 24 characters) that the issuer uses to issue the certificates.
     * (Required)
     * 
     */
    public void setKey(String key) {
        this.key = key;
    }

}

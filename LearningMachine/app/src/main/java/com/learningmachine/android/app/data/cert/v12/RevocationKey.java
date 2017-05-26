
package com.learningmachine.android.app.data.cert.v12;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.joda.time.DateTime;

class RevocationKey {

    /**
     * ISO-8601 formatted date time the key was activated.
     * (Required)
     * 
     */
    @SerializedName("date")
    @Expose
    private DateTime date;
    /**
     * Bitcoin address (compressed public key, usually 24 characters) that the issuer uses to revoke the certificates.
     * (Required)
     * 
     */
    @SerializedName("key")
    @Expose
    private String key;
    /**
     * Optional ISO-8601 formatted date time the key was invalidated.
     * 
     */
    @SerializedName("invalidated")
    @Expose
    private DateTime invalidated;

    /**
     * ISO-8601 formatted date time the key was activated.
     * (Required)
     * 
     */
    public DateTime getDate() {
        return date;
    }

    /**
     * ISO-8601 formatted date time the key was activated.
     * (Required)
     * 
     */
    public void setDate(DateTime date) {
        this.date = date;
    }

    /**
     * Bitcoin address (compressed public key, usually 24 characters) that the issuer uses to revoke the certificates.
     * (Required)
     * 
     */
    public String getKey() {
        return key;
    }

    /**
     * Bitcoin address (compressed public key, usually 24 characters) that the issuer uses to revoke the certificates.
     * (Required)
     * 
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Optional ISO-8601 formatted date time the key was invalidated.
     * 
     */
    public DateTime getInvalidated() {
        return invalidated;
    }

    /**
     * Optional ISO-8601 formatted date time the key was invalidated.
     * 
     */
    public void setInvalidated(DateTime invalidated) {
        this.invalidated = invalidated;
    }

}

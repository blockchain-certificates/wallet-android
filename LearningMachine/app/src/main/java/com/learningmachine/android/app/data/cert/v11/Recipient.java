
package com.learningmachine.android.app.data.cert.v11;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Recipient {

    /**
     * Family name of the recipient.
     * (Required)
     * 
     */
    @SerializedName("familyName")
    @Expose
    private String familyName;
    /**
     * String that represents a recipient's identity. By default, it is an email address.
     * (Required)
     * 
     */
    @SerializedName("identity")
    @Expose
    private String identity;
    /**
     * Type of value in the identity field. Default is 'email'.
     * (Required)
     * 
     */
    @SerializedName("type")
    @Expose
    private String type;
    /**
     * Describes if the value in the identity field is hashed or not. Default is false, indicating that the identity is not hashed. Backcompatible change to allow 2 types that occurred in the wild before proper validation.
     * (Required)
     * 
     */
    @SerializedName("hashed")
    @Expose
    private Object hashed;
    /**
     * Bitcoin address (compressed public key, usually 24 characters) of the recipient.
     * (Required)
     * 
     */
    @SerializedName("pubkey")
    @Expose
    private String pubkey;
    /**
     * Given name of the recipient
     * (Required)
     * 
     */
    @SerializedName("givenName")
    @Expose
    private String givenName;

    /**
     * Family name of the recipient.
     * (Required)
     * 
     */
    public String getFamilyName() {
        return familyName;
    }

    /**
     * Family name of the recipient.
     * (Required)
     * 
     */
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    /**
     * String that represents a recipient's identity. By default, it is an email address.
     * (Required)
     * 
     */
    public String getIdentity() {
        return identity;
    }

    /**
     * String that represents a recipient's identity. By default, it is an email address.
     * (Required)
     * 
     */
    public void setIdentity(String identity) {
        this.identity = identity;
    }

    /**
     * Type of value in the identity field. Default is 'email'.
     * (Required)
     * 
     */
    public String getType() {
        return type;
    }

    /**
     * Type of value in the identity field. Default is 'email'.
     * (Required)
     * 
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Describes if the value in the identity field is hashed or not. Default is false, indicating that the identity is not hashed. Backcompatible change to allow 2 types that occurred in the wild before proper validation.
     * (Required)
     * 
     */
    public Object getHashed() {
        return hashed;
    }

    /**
     * Describes if the value in the identity field is hashed or not. Default is false, indicating that the identity is not hashed. Backcompatible change to allow 2 types that occurred in the wild before proper validation.
     * (Required)
     * 
     */
    public void setHashed(Object hashed) {
        this.hashed = hashed;
    }

    /**
     * Bitcoin address (compressed public key, usually 24 characters) of the recipient.
     * (Required)
     * 
     */
    public String getPubkey() {
        return pubkey;
    }

    /**
     * Bitcoin address (compressed public key, usually 24 characters) of the recipient.
     * (Required)
     * 
     */
    public void setPubkey(String pubkey) {
        this.pubkey = pubkey;
    }

    /**
     * Given name of the recipient
     * (Required)
     * 
     */
    public String getGivenName() {
        return givenName;
    }

    /**
     * Given name of the recipient
     * (Required)
     * 
     */
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

}


package com.learningmachine.android.app.data.cert.v12;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class Recipient {

    /**
     * Family name of the recipient. http://schema.org/Person#familyName
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
     * Describes if the value in the identity field is hashed or not. We strongly recommend that the issuer hash the identy to protect the recipient.
     * (Required)
     * 
     */
    @SerializedName("hashed")
    @Expose
    private boolean hashed;
    /**
     * per the OBI standard, if the recipient identity is hashed, then this should contain the string used to salt the hash
     * 
     */
    @SerializedName("salt")
    @Expose
    private String salt;
    /**
     * Bitcoin address (compressed public key, usually 24 characters) of the recipient. V1.2 change: renamed from pubkey
     * (Required)
     * 
     */
    @SerializedName("publicKey")
    @Expose
    private String publicKey;
    /**
     * Issuer revocation key for the recipient, optional. Bitcoin address (compressed public key, usually 24 characters) of the recipient.
     * 
     */
    @SerializedName("revocationKey")
    @Expose
    private String revocationKey;
    /**
     * Given name of the recipient. http://schema.org/Person#givenName
     * (Required)
     * 
     */
    @SerializedName("givenName")
    @Expose
    private String givenName;

    /**
     * Family name of the recipient. http://schema.org/Person#familyName
     * (Required)
     * 
     */
    public String getFamilyName() {
        return familyName;
    }

    /**
     * Family name of the recipient. http://schema.org/Person#familyName
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
     * Describes if the value in the identity field is hashed or not. We strongly recommend that the issuer hash the identy to protect the recipient.
     * (Required)
     * 
     */
    public boolean isHashed() {
        return hashed;
    }

    /**
     * Describes if the value in the identity field is hashed or not. We strongly recommend that the issuer hash the identy to protect the recipient.
     * (Required)
     * 
     */
    public void setHashed(boolean hashed) {
        this.hashed = hashed;
    }

    /**
     * per the OBI standard, if the recipient identity is hashed, then this should contain the string used to salt the hash
     * 
     */
    public String getSalt() {
        return salt;
    }

    /**
     * per the OBI standard, if the recipient identity is hashed, then this should contain the string used to salt the hash
     * 
     */
    public void setSalt(String salt) {
        this.salt = salt;
    }

    /**
     * Bitcoin address (compressed public key, usually 24 characters) of the recipient. V1.2 change: renamed from pubkey
     * (Required)
     * 
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * Bitcoin address (compressed public key, usually 24 characters) of the recipient. V1.2 change: renamed from pubkey
     * (Required)
     * 
     */
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    /**
     * Issuer revocation key for the recipient, optional. Bitcoin address (compressed public key, usually 24 characters) of the recipient.
     * 
     */
    public String getRevocationKey() {
        return revocationKey;
    }

    /**
     * Issuer revocation key for the recipient, optional. Bitcoin address (compressed public key, usually 24 characters) of the recipient.
     * 
     */
    public void setRevocationKey(String revocationKey) {
        this.revocationKey = revocationKey;
    }

    /**
     * Given name of the recipient. http://schema.org/Person#givenName
     * (Required)
     * 
     */
    public String getGivenName() {
        return givenName;
    }

    /**
     * Given name of the recipient. http://schema.org/Person#givenName
     * (Required)
     * 
     */
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

}

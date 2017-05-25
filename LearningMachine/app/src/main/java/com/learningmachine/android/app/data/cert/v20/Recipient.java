
package com.learningmachine.android.app.data.cert.v20;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;


/**
 * Badge Identity Object
 * <p>
 * From https://w3id.org/openbadges#IdentityObject, with extensions for recipient profile.
 * 
 */
class Recipient {

    /**
     * Defined by `identity` property of https://w3id.org/openbadges#IdentityObject
     * (Required)
     * 
     */
    @SerializedName("identity")
    @Expose
    private Object identity;
    /**
     * Defined by `type` property of https://w3id.org/openbadges#IdentityObject
     * (Required)
     * 
     */
    @SerializedName("type")
    @Expose
    private Recipient.Type type;
    /**
     * Defined by `hashed` property of https://w3id.org/openbadges#IdentityObject
     * (Required)
     * 
     */
    @SerializedName("hashed")
    @Expose
    private boolean hashed;
    /**
     * Defined by `salt` property of https://w3id.org/openbadges#IdentityObject
     * 
     */
    @SerializedName("salt")
    @Expose
    private Object salt;
    /**
     * RecipientProfile schema
     * <p>
     * A Blockcerts extension allowing inclusion of additional recipient details, including recipient publicKey and name. Inclusion of the recipient publicKey allows the recipient to make a strong claim of ownership over the key, and hence the badge being awarded. In the future, publicKey will be deprecated in favor of a decentralized id (DID) in the `id` field.
     * 
     */
    @SerializedName("recipientProfile")
    @Expose
    private RecipientProfile recipientProfile;

    /**
     * Defined by `identity` property of https://w3id.org/openbadges#IdentityObject
     * (Required)
     * 
     */
    public Object getIdentity() {
        return identity;
    }

    /**
     * Defined by `identity` property of https://w3id.org/openbadges#IdentityObject
     * (Required)
     * 
     */
    public void setIdentity(Object identity) {
        this.identity = identity;
    }

    /**
     * Defined by `type` property of https://w3id.org/openbadges#IdentityObject
     * (Required)
     * 
     */
    public Recipient.Type getType() {
        return type;
    }

    /**
     * Defined by `type` property of https://w3id.org/openbadges#IdentityObject
     * (Required)
     * 
     */
    public void setType(Recipient.Type type) {
        this.type = type;
    }

    /**
     * Defined by `hashed` property of https://w3id.org/openbadges#IdentityObject
     * (Required)
     * 
     */
    public boolean isHashed() {
        return hashed;
    }

    /**
     * Defined by `hashed` property of https://w3id.org/openbadges#IdentityObject
     * (Required)
     * 
     */
    public void setHashed(boolean hashed) {
        this.hashed = hashed;
    }

    /**
     * Defined by `salt` property of https://w3id.org/openbadges#IdentityObject
     * 
     */
    public Object getSalt() {
        return salt;
    }

    /**
     * Defined by `salt` property of https://w3id.org/openbadges#IdentityObject
     * 
     */
    public void setSalt(Object salt) {
        this.salt = salt;
    }

    /**
     * RecipientProfile schema
     * <p>
     * A Blockcerts extension allowing inclusion of additional recipient details, including recipient publicKey and name. Inclusion of the recipient publicKey allows the recipient to make a strong claim of ownership over the key, and hence the badge being awarded. In the future, publicKey will be deprecated in favor of a decentralized id (DID) in the `id` field.
     * 
     */
    public RecipientProfile getRecipientProfile() {
        return recipientProfile;
    }

    /**
     * RecipientProfile schema
     * <p>
     * A Blockcerts extension allowing inclusion of additional recipient details, including recipient publicKey and name. Inclusion of the recipient publicKey allows the recipient to make a strong claim of ownership over the key, and hence the badge being awarded. In the future, publicKey will be deprecated in favor of a decentralized id (DID) in the `id` field.
     * 
     */
    public void setRecipientProfile(RecipientProfile recipientProfile) {
        this.recipientProfile = recipientProfile;
    }

    public enum Type {

        @SerializedName("email")
        EMAIL("email");
        private final String value;
        private final static Map<String, Recipient.Type> CONSTANTS = new HashMap<String, Recipient.Type>();

        static {
            for (Recipient.Type c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Type(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public String value() {
            return this.value;
        }

        public static Recipient.Type fromValue(String value) {
            Recipient.Type constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}

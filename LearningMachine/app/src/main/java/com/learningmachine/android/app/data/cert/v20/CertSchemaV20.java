
package com.learningmachine.android.app.data.cert.v20;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.net.URI;


/**
 * Assertion schema
 * <p>
 * Blockcerts 2.0 Assertion schema. Extends Open Badges v2.0 schema: https://w3id.org/openbadges#Assertion
 * 
 */
class CertSchemaV20 {

    /**
     * Defined by `id` property in https://w3id.org/openbadges#Assertion. This may be an HTTP IRI, but only if the issuer plans to host the assertions on a long-term basis, or (at least) until their expiration. Otherwise it is recommended to use a `urn:uuid:<UUID>`-formatted IRI.
     * (Required)
     * 
     */
    @SerializedName("id")
    @Expose
    private String id;
    /**
     * A type or an array of types defined in a referenced JSON-LD context.
     * (Required)
     * 
     */
    @SerializedName("type")
    @Expose
    private Object type;
    /**
     * Badge Identity Object
     * <p>
     * From https://w3id.org/openbadges#IdentityObject, with extensions for recipient profile.
     * (Required)
     * 
     */
    @SerializedName("recipient")
    @Expose
    private Recipient recipient;
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
     * From https://w3id.org/openbadges#BadgeClass
     * (Required)
     * 
     */
    @SerializedName("badge")
    @Expose
    private Badge badge;
    /**
     * From https://w3id.org/openbadges#VerificationObject, with extensions for Blockcerts verification
     * (Required)
     * 
     */
    @SerializedName("verification")
    @Expose
    private Verification verification;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("issuedOn")
    @Expose
    private Object issuedOn;
    /**
     * An image representative of the entity. In Blockcerts this is typically a data URI (https://en.wikipedia.org/wiki/Data_URI_scheme) embedded as a base-64 encoded PNG image, but it may also be a URI where the image may be found.
     * 
     */
    @SerializedName("image")
    @Expose
    private String image;
    /**
     * Defined by `evidence` property of https://w3id.org/openbadges#Assertion
     * 
     */
    @SerializedName("evidence")
    @Expose
    private URI evidence;
    /**
     * Defined by `narrative` property of https://w3id.org/openbadges#Assertion
     * 
     */
    @SerializedName("narrative")
    @Expose
    private String narrative;
    @SerializedName("expires")
    @Expose
    private Object expires;
    @SerializedName("signature")
    @Expose
    private MerkleProof2017Schema signature;

    /**
     * Defined by `id` property in https://w3id.org/openbadges#Assertion. This may be an HTTP IRI, but only if the issuer plans to host the assertions on a long-term basis, or (at least) until their expiration. Otherwise it is recommended to use a `urn:uuid:<UUID>`-formatted IRI.
     * (Required)
     * 
     */
    public String getId() {
        return id;
    }

    /**
     * Defined by `id` property in https://w3id.org/openbadges#Assertion. This may be an HTTP IRI, but only if the issuer plans to host the assertions on a long-term basis, or (at least) until their expiration. Otherwise it is recommended to use a `urn:uuid:<UUID>`-formatted IRI.
     * (Required)
     * 
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * A type or an array of types defined in a referenced JSON-LD context.
     * (Required)
     * 
     */
    public Object getType() {
        return type;
    }

    /**
     * A type or an array of types defined in a referenced JSON-LD context.
     * (Required)
     * 
     */
    public void setType(Object type) {
        this.type = type;
    }

    /**
     * Badge Identity Object
     * <p>
     * From https://w3id.org/openbadges#IdentityObject, with extensions for recipient profile.
     * (Required)
     * 
     */
    public Recipient getRecipient() {
        return recipient;
    }

    /**
     * Badge Identity Object
     * <p>
     * From https://w3id.org/openbadges#IdentityObject, with extensions for recipient profile.
     * (Required)
     * 
     */
    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }

    public RecipientProfile getRecipientProfile() {
        if (recipientProfile != null) {
            return recipientProfile;
        } else if (recipient != null) {
            return recipient.getRecipientProfile();
        } else {
            return null;
        }
    }

    public void setRecipientProfile(RecipientProfile recipientProfile) {
        this.recipientProfile = recipientProfile;
    }

    /**
     * From https://w3id.org/openbadges#BadgeClass
     * (Required)
     * 
     */
    public Badge getBadge() {
        return badge;
    }

    /**
     * From https://w3id.org/openbadges#BadgeClass
     * (Required)
     * 
     */
    public void setBadge(Badge badge) {
        this.badge = badge;
    }

    /**
     * From https://w3id.org/openbadges#VerificationObject, with extensions for Blockcerts verification
     * (Required)
     * 
     */
    public Verification getVerification() {
        return verification;
    }

    /**
     * From https://w3id.org/openbadges#VerificationObject, with extensions for Blockcerts verification
     * (Required)
     * 
     */
    public void setVerification(Verification verification) {
        this.verification = verification;
    }

    /**
     * 
     * (Required)
     * 
     */
    public Object getIssuedOn() {
        return issuedOn;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setIssuedOn(Object issuedOn) {
        this.issuedOn = issuedOn;
    }

    /**
     * An image representative of the entity. In Blockcerts this is typically a data URI (https://en.wikipedia.org/wiki/Data_URI_scheme) embedded as a base-64 encoded PNG image, but it may also be a URI where the image may be found.
     * 
     */
    public String getImage() {
        return image;
    }

    /**
     * An image representative of the entity. In Blockcerts this is typically a data URI (https://en.wikipedia.org/wiki/Data_URI_scheme) embedded as a base-64 encoded PNG image, but it may also be a URI where the image may be found.
     * 
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Defined by `evidence` property of https://w3id.org/openbadges#Assertion
     * 
     */
    public URI getEvidence() {
        return evidence;
    }

    /**
     * Defined by `evidence` property of https://w3id.org/openbadges#Assertion
     * 
     */
    public void setEvidence(URI evidence) {
        this.evidence = evidence;
    }

    /**
     * Defined by `narrative` property of https://w3id.org/openbadges#Assertion
     * 
     */
    public String getNarrative() {
        return narrative;
    }

    /**
     * Defined by `narrative` property of https://w3id.org/openbadges#Assertion
     * 
     */
    public void setNarrative(String narrative) {
        this.narrative = narrative;
    }

    public Object getExpires() {
        return expires;
    }

    public void setExpires(Object expires) {
        this.expires = expires;
    }

    public MerkleProof2017Schema getSignature() {
        return signature;
    }

    public void setSignature(MerkleProof2017Schema signature) {
        this.signature = signature;
    }
}

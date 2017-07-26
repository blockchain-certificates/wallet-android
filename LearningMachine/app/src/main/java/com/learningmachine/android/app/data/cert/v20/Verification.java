
package com.learningmachine.android.app.data.cert.v20;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.net.URI;


/**
 * From https://w3id.org/openbadges#VerificationObject, with extensions for Blockcerts verification
 * 
 */
class Verification {

    /**
     * A type or an array of types defined in a referenced JSON-LD context.
     * (Required)
     * 
     */
    @SerializedName("type")
    @Expose
    private Object type;
    /**
     * Blockcerts extension: the expected blockchain address for the signer of the transaction containing the merkle proof. In Blockcerts `creator` IRIs are typically represented with a `<scheme>:` prefix. For Bitcoin transactions, this would be the issuer public Bitcoin address prefixed with `ecdsa-koblitz-pubkey:`; e.g. `ecdsa-koblitz-pubkey:14RZvYazz9H2DC2skBfpPVxax54g4yabxe`
     * 
     */
    @SerializedName(value = "publicKey", alternate = {"creator"})
    @Expose
    private String publicKey;
    /**
     * Defined by `verificationProperty` property of https://w3id.org/openbadges#VerificationObject
     * 
     */
    @SerializedName("verificationProperty")
    @Expose
    private VerificationProperty verificationProperty;
    /**
     * Defined by `startsWith` property of https://w3id.org/openbadges#VerificationObject
     * 
     */
    @SerializedName("startsWith")
    @Expose
    private URI startsWith;
    /**
     * Defined by `allowedOrigins` property of https://w3id.org/openbadges#VerificationObject
     * 
     */
    @SerializedName("allowedOrigins")
    @Expose
    private URI allowedOrigins;

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
     * Blockcerts extension: the expected blockchain address for the signer of the transaction containing the merkle proof. In Blockcerts `creator` IRIs are typically represented with a `<scheme>:` prefix. For Bitcoin transactions, this would be the issuer public Bitcoin address prefixed with `ecdsa-koblitz-pubkey:`; e.g. `ecdsa-koblitz-pubkey:14RZvYazz9H2DC2skBfpPVxax54g4yabxe`
     * 
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * Blockcerts extension: the expected blockchain address for the signer of the transaction containing the merkle proof. In Blockcerts `creator` IRIs are typically represented with a `<scheme>:` prefix. For Bitcoin transactions, this would be the issuer public Bitcoin address prefixed with `ecdsa-koblitz-pubkey:`; e.g. `ecdsa-koblitz-pubkey:14RZvYazz9H2DC2skBfpPVxax54g4yabxe`
     * 
     */
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    /**
     * Defined by `verificationProperty` property of https://w3id.org/openbadges#VerificationObject
     * 
     */
    public VerificationProperty getVerificationProperty() {
        return verificationProperty;
    }

    /**
     * Defined by `verificationProperty` property of https://w3id.org/openbadges#VerificationObject
     * 
     */
    public void setVerificationProperty(VerificationProperty verificationProperty) {
        this.verificationProperty = verificationProperty;
    }

    /**
     * Defined by `startsWith` property of https://w3id.org/openbadges#VerificationObject
     * 
     */
    public URI getStartsWith() {
        return startsWith;
    }

    /**
     * Defined by `startsWith` property of https://w3id.org/openbadges#VerificationObject
     * 
     */
    public void setStartsWith(URI startsWith) {
        this.startsWith = startsWith;
    }

    /**
     * Defined by `allowedOrigins` property of https://w3id.org/openbadges#VerificationObject
     * 
     */
    public URI getAllowedOrigins() {
        return allowedOrigins;
    }

    /**
     * Defined by `allowedOrigins` property of https://w3id.org/openbadges#VerificationObject
     * 
     */
    public void setAllowedOrigins(URI allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

}

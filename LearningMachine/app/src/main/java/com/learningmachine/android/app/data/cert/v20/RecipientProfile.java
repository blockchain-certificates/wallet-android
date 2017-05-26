
package com.learningmachine.android.app.data.cert.v20;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.net.URI;


/**
 * RecipientProfile schema
 * <p>
 * A Blockcerts extension allowing inclusion of additional recipient details, including recipient publicKey and name. Inclusion of the recipient publicKey allows the recipient to make a strong claim of ownership over the key, and hence the badge being awarded. In the future, publicKey will be deprecated in favor of a decentralized id (DID) in the `id` field.
 * 
 */
class RecipientProfile {

    /**
     * reserved for future use as DID
     * 
     */
    @SerializedName("id")
    @Expose
    private String id;
    /**
     * Name of recipient, http://schema.org/name
     * 
     */
    @SerializedName("name")
    @Expose
    private String name;
    /**
     * In Blockcerts `publicKey` IRIs are typically represented with a `<scheme>:` prefix. For Bitcoin transactions, this would be the recipient public Bitcoin address prefixed with `ecdsa-koblitz-pubkey:`; e.g. `ecdsa-koblitz-pubkey:14RZvYazz9H2DC2skBfpPVxax54g4yabxe`
     * 
     */
    @SerializedName("publicKey")
    @Expose
    private URI publicKey;

    /**
     * reserved for future use as DID
     * 
     */
    public String getId() {
        return id;
    }

    /**
     * reserved for future use as DID
     * 
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Name of recipient, http://schema.org/name
     * 
     */
    public String getName() {
        return name;
    }

    /**
     * Name of recipient, http://schema.org/name
     * 
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * In Blockcerts `publicKey` IRIs are typically represented with a `<scheme>:` prefix. For Bitcoin transactions, this would be the recipient public Bitcoin address prefixed with `ecdsa-koblitz-pubkey:`; e.g. `ecdsa-koblitz-pubkey:14RZvYazz9H2DC2skBfpPVxax54g4yabxe`
     * 
     */
    public URI getPublicKey() {
        return publicKey;
    }

    /**
     * In Blockcerts `publicKey` IRIs are typically represented with a `<scheme>:` prefix. For Bitcoin transactions, this would be the recipient public Bitcoin address prefixed with `ecdsa-koblitz-pubkey:`; e.g. `ecdsa-koblitz-pubkey:14RZvYazz9H2DC2skBfpPVxax54g4yabxe`
     * 
     */
    public void setPublicKey(URI publicKey) {
        this.publicKey = publicKey;
    }

}

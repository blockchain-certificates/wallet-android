
package com.learningmachine.android.app.data.cert.v11;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.net.URI;

public class Verify {

    /**
     * Name of the attribute in the json that is signed by the issuer's private key. Default is 'uid', referring to the uid attribute.
     * (Required)
     * 
     */
    @SerializedName("attribute-signed")
    @Expose
    private String attributeSigned;
    /**
     * Name of the signing method. Default is 'ECDSA(secp256k1)', referring to the Bitcoin method of signing messages with the issuer's private key.
     * (Required)
     * 
     */
    @SerializedName("type")
    @Expose
    private String type;
    /**
     * URI where issuer's public key is presented. Default is https://[domain]/keys/[org-abbr]-certs-public-key.asc. Compatible with open badges specification v1.1. Ideally, we would change this to point to a JSON instead, so we could retire keys (similar to the way we handle the issuer ID), but for now we're sticking with the OBS 1.1.
     * (Required)
     * 
     */
    @SerializedName("signer")
    @Expose
    private URI signer;

    /**
     * Name of the attribute in the json that is signed by the issuer's private key. Default is 'uid', referring to the uid attribute.
     * (Required)
     * 
     */
    public String getAttributeSigned() {
        return attributeSigned;
    }

    /**
     * Name of the attribute in the json that is signed by the issuer's private key. Default is 'uid', referring to the uid attribute.
     * (Required)
     * 
     */
    public void setAttributeSigned(String attributeSigned) {
        this.attributeSigned = attributeSigned;
    }

    /**
     * Name of the signing method. Default is 'ECDSA(secp256k1)', referring to the Bitcoin method of signing messages with the issuer's private key.
     * (Required)
     * 
     */
    public String getType() {
        return type;
    }

    /**
     * Name of the signing method. Default is 'ECDSA(secp256k1)', referring to the Bitcoin method of signing messages with the issuer's private key.
     * (Required)
     * 
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * URI where issuer's public key is presented. Default is https://[domain]/keys/[org-abbr]-certs-public-key.asc. Compatible with open badges specification v1.1. Ideally, we would change this to point to a JSON instead, so we could retire keys (similar to the way we handle the issuer ID), but for now we're sticking with the OBS 1.1.
     * (Required)
     * 
     */
    public URI getSigner() {
        return signer;
    }

    /**
     * URI where issuer's public key is presented. Default is https://[domain]/keys/[org-abbr]-certs-public-key.asc. Compatible with open badges specification v1.1. Ideally, we would change this to point to a JSON instead, so we could retire keys (similar to the way we handle the issuer ID), but for now we're sticking with the OBS 1.1.
     * (Required)
     * 
     */
    public void setSigner(URI signer) {
        this.signer = signer;
    }

}

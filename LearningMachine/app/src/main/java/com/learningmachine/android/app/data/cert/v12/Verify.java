
package com.learningmachine.android.app.data.cert.v12;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * V1.2 notice: the Blockchain Certificates VerificationObject will change in the next schema version to be consistent with OBI VerificationObjects. This work is in progress.
 * 
 */
class Verify {

    /**
     * Name of the attribute in the json that is signed by the issuer's private key. Default is 'uid', referring to the uid attribute.
     * (Required)
     * 
     */
    @SerializedName("attribute-signed")
    @Expose
    private String attributeSigned;
    /**
     * Name of the signing method. Default is 'ECDSA(secp256k1)', referring to the Blockchain Certificates method of signing messages with the issuer's private key.
     * (Required)
     * 
     */
    @SerializedName("type")
    @Expose
    private Verify.Type type;
    /**
     * URI where issuer's public key is presented. Default is https://[domain]/keys/[org-abbr]-certs-public-key.asc. V1.2 change: this field is optional
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
     * Name of the signing method. Default is 'ECDSA(secp256k1)', referring to the Blockchain Certificates method of signing messages with the issuer's private key.
     * (Required)
     * 
     */
    public Verify.Type getType() {
        return type;
    }

    /**
     * Name of the signing method. Default is 'ECDSA(secp256k1)', referring to the Blockchain Certificates method of signing messages with the issuer's private key.
     * (Required)
     * 
     */
    public void setType(Verify.Type type) {
        this.type = type;
    }

    /**
     * URI where issuer's public key is presented. Default is https://[domain]/keys/[org-abbr]-certs-public-key.asc. V1.2 change: this field is optional
     * 
     */
    public URI getSigner() {
        return signer;
    }

    /**
     * URI where issuer's public key is presented. Default is https://[domain]/keys/[org-abbr]-certs-public-key.asc. V1.2 change: this field is optional
     * 
     */
    public void setSigner(URI signer) {
        this.signer = signer;
    }

    public enum Type {

        @SerializedName("hosted")
        HOSTED("hosted"),
        @SerializedName("signed")
        SIGNED("signed"),
        @SerializedName("ECDSA(secp256k1)")
        ECDSA_SECP_256_K_1("ECDSA(secp256k1)");
        private final String value;
        private final static Map<String, Verify.Type> CONSTANTS = new HashMap<String, Verify.Type>();

        static {
            for (Verify.Type c: values()) {
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

        public static Verify.Type fromValue(String value) {
            Verify.Type constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}

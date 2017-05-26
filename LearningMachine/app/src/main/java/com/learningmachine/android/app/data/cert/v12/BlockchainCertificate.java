
package com.learningmachine.android.app.data.cert.v12;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Blockchain Certificates Version 1.2 Schema
 * <p>
 * A schema for representing certificates on the blockchain
 * 
 */
class BlockchainCertificate {

    /**
     * A link to a valid JSON-LD context file, that maps term names to contexts. Blockchain Certificate contexts may also define JSON-schema to validate Blockchain Certificates against. In a Blockchain Certificate Object, this will almost always be a string:uri to a single context file, but might rarely be an array of links or context objects instead. This schema also allows direct mapping of terms to IRIs by using an object as an option within an array.
     * (Required)
     * 
     */
    @SerializedName("@context")
    @Expose
    private Object context;
    /**
     * A type or an array of types that the Blockchain Certificate object represents. The first or only item should be 'BlockchainCertificate', and any others should each be an IRI (usually a URL) corresponding to a definition of the type itself. In almost all cases, there will be only one type: 'BlockchainCertificate'
     * (Required)
     * 
     */
    @SerializedName("type")
    @Expose
    private Object type;
    /**
     * Certificate Document Version 1.2 Schema
     * <p>
     * The complete certificate document, including the assertion, certificate, and issuer. Doesn't include the blockchain receipt. This part is hashed and placed on the blockchain
     * (Required)
     * 
     */
    @SerializedName("document")
    @Expose
    private Document document;
    /**
     * Blockchain Certificates Receipt Schema, Version 1.2
     * <p>
     * Provides evidence of the certificate on the blockchain, using the chainpoint v2 standard
     * (Required)
     * 
     */
    @SerializedName("receipt")
    @Expose
    private Receipt receipt;

    /**
     * A link to a valid JSON-LD context file, that maps term names to contexts. Blockchain Certificate contexts may also define JSON-schema to validate Blockchain Certificates against. In a Blockchain Certificate Object, this will almost always be a string:uri to a single context file, but might rarely be an array of links or context objects instead. This schema also allows direct mapping of terms to IRIs by using an object as an option within an array.
     * (Required)
     * 
     */
    public Object getContext() {
        return context;
    }

    /**
     * A link to a valid JSON-LD context file, that maps term names to contexts. Blockchain Certificate contexts may also define JSON-schema to validate Blockchain Certificates against. In a Blockchain Certificate Object, this will almost always be a string:uri to a single context file, but might rarely be an array of links or context objects instead. This schema also allows direct mapping of terms to IRIs by using an object as an option within an array.
     * (Required)
     * 
     */
    public void setContext(Object context) {
        this.context = context;
    }

    /**
     * A type or an array of types that the Blockchain Certificate object represents. The first or only item should be 'BlockchainCertificate', and any others should each be an IRI (usually a URL) corresponding to a definition of the type itself. In almost all cases, there will be only one type: 'BlockchainCertificate'
     * (Required)
     * 
     */
    public Object getType() {
        return type;
    }

    /**
     * A type or an array of types that the Blockchain Certificate object represents. The first or only item should be 'BlockchainCertificate', and any others should each be an IRI (usually a URL) corresponding to a definition of the type itself. In almost all cases, there will be only one type: 'BlockchainCertificate'
     * (Required)
     * 
     */
    public void setType(Object type) {
        this.type = type;
    }

    /**
     * Certificate Document Version 1.2 Schema
     * <p>
     * The complete certificate document, including the assertion, certificate, and issuer. Doesn't include the blockchain receipt. This part is hashed and placed on the blockchain
     * (Required)
     * 
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Certificate Document Version 1.2 Schema
     * <p>
     * The complete certificate document, including the assertion, certificate, and issuer. Doesn't include the blockchain receipt. This part is hashed and placed on the blockchain
     * (Required)
     * 
     */
    public void setDocument(Document document) {
        this.document = document;
    }

    /**
     * Blockchain Certificates Receipt Schema, Version 1.2
     * <p>
     * Provides evidence of the certificate on the blockchain, using the chainpoint v2 standard
     * (Required)
     * 
     */
    public Receipt getReceipt() {
        return receipt;
    }

    /**
     * Blockchain Certificates Receipt Schema, Version 1.2
     * <p>
     * Provides evidence of the certificate on the blockchain, using the chainpoint v2 standard
     * (Required)
     * 
     */
    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
    }

}


package com.learningmachine.android.app.data.cert.v12;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.net.URI;


/**
 * Blockchain Certificates Assertion Schema, Version 1.2
 * <p>
 * Extends the Open Badges Assertion Schema for certificates on the blockchain
 * 
 */
class Assertion {

    /**
     * A link to a valid JSON-LD context file, that maps term names to contexts. Blockchain Certificate contexts may also define JSON-schema to validate Blockchain Certificates against. In a Blockchain Certificate Object, this will almost always be a string:uri to a single context file, but might rarely be an array of links or context objects instead. This schema also allows direct mapping of terms to IRIs by using an object as an option within an array.
     * 
     */
    @SerializedName("@context")
    @Expose
    private Object context;
    /**
     * A type or an array of types that the Blockchain Certificate object represents. The first or only item should be 'Assertion', and any others should each be an IRI (usually a URL) corresponding to a definition of the type itself. In almost all cases, there will be only one type: 'Assertion'
     * (Required)
     * 
     */
    @SerializedName("type")
    @Expose
    private Object type;
    /**
     * URI that links to the certificate on the viewer.
     * (Required)
     * 
     */
    @SerializedName("id")
    @Expose
    private URI id;
    /**
     * Unique identifier, in GUID format. V1.2 change: string pattern changed to guid
     * (Required)
     * 
     */
    @SerializedName("uid")
    @Expose
    private String uid;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("issuedOn")
    @Expose
    private String issuedOn;
    /**
     * URL of the work that the recipient did to earn the achievement. This can be a page that links out to other pages if linking directly to the work is infeasible. V1.2 made this field optional, which is consistent with OBI spec.
     * 
     */
    @SerializedName("evidence")
    @Expose
    private URI evidence;
    @SerializedName("expires")
    @Expose
    private Object expires;
    /**
     * A single signature image, or array of objects with fields image and jobTitle. V1.2 change: support multiple signatures
     * 
     */
    @SerializedName("image:signature")
    @Expose
    private Object imageSignature;

    /**
     * A link to a valid JSON-LD context file, that maps term names to contexts. Blockchain Certificate contexts may also define JSON-schema to validate Blockchain Certificates against. In a Blockchain Certificate Object, this will almost always be a string:uri to a single context file, but might rarely be an array of links or context objects instead. This schema also allows direct mapping of terms to IRIs by using an object as an option within an array.
     * 
     */
    public Object getContext() {
        return context;
    }

    /**
     * A link to a valid JSON-LD context file, that maps term names to contexts. Blockchain Certificate contexts may also define JSON-schema to validate Blockchain Certificates against. In a Blockchain Certificate Object, this will almost always be a string:uri to a single context file, but might rarely be an array of links or context objects instead. This schema also allows direct mapping of terms to IRIs by using an object as an option within an array.
     * 
     */
    public void setContext(Object context) {
        this.context = context;
    }

    /**
     * A type or an array of types that the Blockchain Certificate object represents. The first or only item should be 'Assertion', and any others should each be an IRI (usually a URL) corresponding to a definition of the type itself. In almost all cases, there will be only one type: 'Assertion'
     * (Required)
     * 
     */
    public Object getType() {
        return type;
    }

    /**
     * A type or an array of types that the Blockchain Certificate object represents. The first or only item should be 'Assertion', and any others should each be an IRI (usually a URL) corresponding to a definition of the type itself. In almost all cases, there will be only one type: 'Assertion'
     * (Required)
     * 
     */
    public void setType(Object type) {
        this.type = type;
    }

    /**
     * URI that links to the certificate on the viewer.
     * (Required)
     * 
     */
    public URI getId() {
        return id;
    }

    /**
     * URI that links to the certificate on the viewer.
     * (Required)
     * 
     */
    public void setId(URI id) {
        this.id = id;
    }

    /**
     * Unique identifier, in GUID format. V1.2 change: string pattern changed to guid
     * (Required)
     * 
     */
    public String getUid() {
        return uid;
    }

    /**
     * Unique identifier, in GUID format. V1.2 change: string pattern changed to guid
     * (Required)
     * 
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getIssuedOn() {
        return issuedOn;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setIssuedOn(String issuedOn) {
        this.issuedOn = issuedOn;
    }

    /**
     * URL of the work that the recipient did to earn the achievement. This can be a page that links out to other pages if linking directly to the work is infeasible. V1.2 made this field optional, which is consistent with OBI spec.
     * 
     */
    public URI getEvidence() {
        return evidence;
    }

    /**
     * URL of the work that the recipient did to earn the achievement. This can be a page that links out to other pages if linking directly to the work is infeasible. V1.2 made this field optional, which is consistent with OBI spec.
     * 
     */
    public void setEvidence(URI evidence) {
        this.evidence = evidence;
    }

    public Object getExpires() {
        return expires;
    }

    public void setExpires(Object expires) {
        this.expires = expires;
    }

    /**
     * A single signature image, or array of objects with fields image and jobTitle. V1.2 change: support multiple signatures
     * 
     */
    public Object getImageSignature() {
        return imageSignature;
    }

    /**
     * A single signature image, or array of objects with fields image and jobTitle. V1.2 change: support multiple signatures
     * 
     */
    public void setImageSignature(Object imageSignature) {
        this.imageSignature = imageSignature;
    }

}

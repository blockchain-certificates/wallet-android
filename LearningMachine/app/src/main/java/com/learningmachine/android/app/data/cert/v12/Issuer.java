
package com.learningmachine.android.app.data.cert.v12;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.net.URI;


/**
 * Blockchain Certificates Issuer Schema, Version 1.2
 * <p>
 * Extends the Open Badges Issuer Schema for certificates on the blockchain
 * 
 */
class Issuer {

    /**
     * A link to a valid JSON-LD context file, that maps term names to contexts. Blockchain Certificate contexts may also define JSON-schema to validate Blockchain Certificates against. In a Blockchain Certificate Object, this will almost always be a string:uri to a single context file, but might rarely be an array of links or context objects instead. This schema also allows direct mapping of terms to IRIs by using an object as an option within an array.
     * 
     */
    @SerializedName("@context")
    @Expose
    private Object context;
    /**
     * A type or an array of types that the Blockchain Certificate object represents. The first or only item should be 'Issuer', and any others should each be an IRI (usually a URL) corresponding to a definition of the type itself. In almost all cases, there will be only one type: 'Issuer'
     * 
     */
    @SerializedName("type")
    @Expose
    private Object type;
    /**
     * Link to a JSON that details the issuer's issuing and recovation keys. Default is https://[domain]/issuer/[org_abbr]-issuer.json. Included for (near) backward compatibility with open badges specification 1.1
     * (Required)
     * 
     */
    @SerializedName("id")
    @Expose
    private URI id;
    /**
     * An image representative of the entity. This overrides BadgeImage from OBI because oneOf, compared to anyOf, was failing validation
     * 
     */
    @SerializedName("image")
    @Expose
    private String image;
    /**
     * Human-readable name of the issuing entity
     * (Required)
     * 
     */
    @SerializedName("name")
    @Expose
    private String name;
    /**
     * The URL of the issuer's website or homepage
     * (Required)
     * 
     */
    @SerializedName("url")
    @Expose
    private URI url;
    /**
     * A text description of the issuing organization
     * 
     */
    @SerializedName("description")
    @Expose
    private String description;
    /**
     * Contact address for the individual or organization.
     * 
     */
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("revocationList")
    @Expose
    private URI revocationList;

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
     * A type or an array of types that the Blockchain Certificate object represents. The first or only item should be 'Issuer', and any others should each be an IRI (usually a URL) corresponding to a definition of the type itself. In almost all cases, there will be only one type: 'Issuer'
     * 
     */
    public Object getType() {
        return type;
    }

    /**
     * A type or an array of types that the Blockchain Certificate object represents. The first or only item should be 'Issuer', and any others should each be an IRI (usually a URL) corresponding to a definition of the type itself. In almost all cases, there will be only one type: 'Issuer'
     * 
     */
    public void setType(Object type) {
        this.type = type;
    }

    /**
     * Link to a JSON that details the issuer's issuing and recovation keys. Default is https://[domain]/issuer/[org_abbr]-issuer.json. Included for (near) backward compatibility with open badges specification 1.1
     * (Required)
     * 
     */
    public URI getId() {
        return id;
    }

    /**
     * Link to a JSON that details the issuer's issuing and recovation keys. Default is https://[domain]/issuer/[org_abbr]-issuer.json. Included for (near) backward compatibility with open badges specification 1.1
     * (Required)
     * 
     */
    public void setId(URI id) {
        this.id = id;
    }

    /**
     * An image representative of the entity. This overrides BadgeImage from OBI because oneOf, compared to anyOf, was failing validation
     * 
     */
    public String getImage() {
        return image;
    }

    /**
     * An image representative of the entity. This overrides BadgeImage from OBI because oneOf, compared to anyOf, was failing validation
     * 
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Human-readable name of the issuing entity
     * (Required)
     * 
     */
    public String getName() {
        return name;
    }

    /**
     * Human-readable name of the issuing entity
     * (Required)
     * 
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The URL of the issuer's website or homepage
     * (Required)
     * 
     */
    public URI getUrl() {
        return url;
    }

    /**
     * The URL of the issuer's website or homepage
     * (Required)
     * 
     */
    public void setUrl(URI url) {
        this.url = url;
    }

    /**
     * A text description of the issuing organization
     * 
     */
    public String getDescription() {
        return description;
    }

    /**
     * A text description of the issuing organization
     * 
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Contact address for the individual or organization.
     * 
     */
    public String getEmail() {
        return email;
    }

    /**
     * Contact address for the individual or organization.
     * 
     */
    public void setEmail(String email) {
        this.email = email;
    }

    public URI getRevocationList() {
        return revocationList;
    }

    public void setRevocationList(URI revocationList) {
        this.revocationList = revocationList;
    }

}

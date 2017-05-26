
package com.learningmachine.android.app.data.cert.v12;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


/**
 * Blockchain Certificates Certificate Schema, Version 1.2
 * <p>
 * Extends the Open Badges Certificate Schema for certificates on the blockchain
 * 
 */
class Certificate {

    /**
     * A link to a valid JSON-LD context file, that maps term names to contexts. Blockchain Certificate contexts may also define JSON-schema to validate Blockchain Certificates against. In a Blockchain Certificate Object, this will almost always be a string:uri to a single context file, but might rarely be an array of links or context objects instead. This schema also allows direct mapping of terms to IRIs by using an object as an option within an array.
     * 
     */
    @SerializedName("@context")
    @Expose
    private Object context;
    /**
     * A type or an array of types that the Blockchain Certificate object represents. The first or only item should be 'Certificate', and any others should each be an IRI (usually a URL) corresponding to a definition of the type itself. In almost all cases, there will be only one type: 'Certificate'
     * 
     */
    @SerializedName("type")
    @Expose
    private Object type;
    /**
     * URI link to a JSON that describes the type of certificate. Default format is https://[domain]/criteria/[year]/[month]/[certificate_title].json. V1.2 change: this field is optional.
     * 
     */
    @SerializedName("id")
    @Expose
    private URI id;
    /**
     * Name (or title) of the certificate. V1.2 change: renamed from 'title' to be consistent with OBI schema
     * (Required)
     * 
     */
    @SerializedName("name")
    @Expose
    private String name;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("description")
    @Expose
    private String description;
    /**
     * Data URI; a base-64 encoded png image of the certificate's image. https://en.wikipedia.org/wiki/Data_URI_scheme
     * (Required)
     * 
     */
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("criteria")
    @Expose
    private URI criteria;
    /**
     * Blockchain Certificates Issuer Schema, Version 1.2
     * <p>
     * Extends the Open Badges Issuer Schema for certificates on the blockchain
     * (Required)
     * 
     */
    @SerializedName("issuer")
    @Expose
    private Issuer issuer;
    /**
     * List of objects describing which educational standards this badge aligns to, if any, and linking to the appropriate standard.
     * 
     */
    @SerializedName("alignment")
    @Expose
    private List<Alignment> alignment = new ArrayList<Alignment>();
    /**
     * An array of strings. TagsArray is not a controlled vocabulary; it's a folksonomy
     * 
     */
    @SerializedName("tags")
    @Expose
    private Set<String> tags = new LinkedHashSet<String>();
    /**
     * Represents the ieft language and ieft country codes. Format is [ieft_language]-[IEFT_COUNTRY]. V1.2 changes: this field is optional
     * 
     */
    @SerializedName("language")
    @Expose
    private String language;
    /**
     * Subtitle of the certificate. V1.2 changes: this type is now string, and this field is optional
     * 
     */
    @SerializedName("subtitle")
    @Expose
    private String subtitle;

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
     * A type or an array of types that the Blockchain Certificate object represents. The first or only item should be 'Certificate', and any others should each be an IRI (usually a URL) corresponding to a definition of the type itself. In almost all cases, there will be only one type: 'Certificate'
     * 
     */
    public Object getType() {
        return type;
    }

    /**
     * A type or an array of types that the Blockchain Certificate object represents. The first or only item should be 'Certificate', and any others should each be an IRI (usually a URL) corresponding to a definition of the type itself. In almost all cases, there will be only one type: 'Certificate'
     * 
     */
    public void setType(Object type) {
        this.type = type;
    }

    /**
     * URI link to a JSON that describes the type of certificate. Default format is https://[domain]/criteria/[year]/[month]/[certificate_title].json. V1.2 change: this field is optional.
     * 
     */
    public URI getId() {
        return id;
    }

    /**
     * URI link to a JSON that describes the type of certificate. Default format is https://[domain]/criteria/[year]/[month]/[certificate_title].json. V1.2 change: this field is optional.
     * 
     */
    public void setId(URI id) {
        this.id = id;
    }

    /**
     * Name (or title) of the certificate. V1.2 change: renamed from 'title' to be consistent with OBI schema
     * (Required)
     * 
     */
    public String getName() {
        return name;
    }

    /**
     * Name (or title) of the certificate. V1.2 change: renamed from 'title' to be consistent with OBI schema
     * (Required)
     * 
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getDescription() {
        return description;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Data URI; a base-64 encoded png image of the certificate's image. https://en.wikipedia.org/wiki/Data_URI_scheme
     * (Required)
     * 
     */
    public String getImage() {
        return image;
    }

    /**
     * Data URI; a base-64 encoded png image of the certificate's image. https://en.wikipedia.org/wiki/Data_URI_scheme
     * (Required)
     * 
     */
    public void setImage(String image) {
        this.image = image;
    }

    public URI getCriteria() {
        return criteria;
    }

    public void setCriteria(URI criteria) {
        this.criteria = criteria;
    }

    /**
     * Blockchain Certificates Issuer Schema, Version 1.2
     * <p>
     * Extends the Open Badges Issuer Schema for certificates on the blockchain
     * (Required)
     * 
     */
    public Issuer getIssuer() {
        return issuer;
    }

    /**
     * Blockchain Certificates Issuer Schema, Version 1.2
     * <p>
     * Extends the Open Badges Issuer Schema for certificates on the blockchain
     * (Required)
     * 
     */
    public void setIssuer(Issuer issuer) {
        this.issuer = issuer;
    }

    /**
     * List of objects describing which educational standards this badge aligns to, if any, and linking to the appropriate standard.
     * 
     */
    public List<Alignment> getAlignment() {
        return alignment;
    }

    /**
     * List of objects describing which educational standards this badge aligns to, if any, and linking to the appropriate standard.
     * 
     */
    public void setAlignment(List<Alignment> alignment) {
        this.alignment = alignment;
    }

    /**
     * An array of strings. TagsArray is not a controlled vocabulary; it's a folksonomy
     * 
     */
    public Set<String> getTags() {
        return tags;
    }

    /**
     * An array of strings. TagsArray is not a controlled vocabulary; it's a folksonomy
     * 
     */
    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    /**
     * Represents the ieft language and ieft country codes. Format is [ieft_language]-[IEFT_COUNTRY]. V1.2 changes: this field is optional
     * 
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Represents the ieft language and ieft country codes. Format is [ieft_language]-[IEFT_COUNTRY]. V1.2 changes: this field is optional
     * 
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Subtitle of the certificate. V1.2 changes: this type is now string, and this field is optional
     * 
     */
    public String getSubtitle() {
        return subtitle;
    }

    /**
     * Subtitle of the certificate. V1.2 changes: this type is now string, and this field is optional
     * 
     */
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

}

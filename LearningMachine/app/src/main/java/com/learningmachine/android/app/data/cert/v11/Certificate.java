
package com.learningmachine.android.app.data.cert.v11;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.net.URI;

public class Certificate {

    /**
     * URI link to a JSON that describes the type of certificate. Default format is https://[domain]/criteria/[year]/[month]/[certificate_title].json.
     * (Required)
     * 
     */
    @SerializedName("id")
    @Expose
    private URI id;
    /**
     * A base-64 encoded png image of the certificate's image.
     * (Required)
     * 
     */
    @SerializedName("image")
    @Expose
    private String image;
    /**
     * Represents the ieft language and ieft country codes. Format is [ieft_language]-[IEFT_COUNTRY]. Backcompatible change to make this field not required.
     * 
     */
    @SerializedName("language")
    @Expose
    private String language;
    /**
     * Subtitle of the certificate.
     * (Required)
     * 
     */
    @SerializedName("subtitle")
    @Expose
    private Subtitle subtitle;
    /**
     * Title of the certificate.
     * (Required)
     * 
     */
    @SerializedName("title")
    @Expose
    private String title;
    /**
     * Details about the issuer of the certificate.
     * (Required)
     * 
     */
    @SerializedName("issuer")
    @Expose
    private Issuer issuer;
    /**
     * Description of what the certificate represents. Usually one - three sentences long.
     * (Required)
     * 
     */
    @SerializedName("description")
    @Expose
    private String description;

    /**
     * URI link to a JSON that describes the type of certificate. Default format is https://[domain]/criteria/[year]/[month]/[certificate_title].json.
     * (Required)
     * 
     */
    public URI getId() {
        return id;
    }

    /**
     * URI link to a JSON that describes the type of certificate. Default format is https://[domain]/criteria/[year]/[month]/[certificate_title].json.
     * (Required)
     * 
     */
    public void setId(URI id) {
        this.id = id;
    }

    /**
     * A base-64 encoded png image of the certificate's image.
     * (Required)
     * 
     */
    public String getImage() {
        return image;
    }

    /**
     * A base-64 encoded png image of the certificate's image.
     * (Required)
     * 
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Represents the ieft language and ieft country codes. Format is [ieft_language]-[IEFT_COUNTRY]. Backcompatible change to make this field not required.
     * 
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Represents the ieft language and ieft country codes. Format is [ieft_language]-[IEFT_COUNTRY]. Backcompatible change to make this field not required.
     * 
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Subtitle of the certificate.
     * (Required)
     * 
     */
    public Subtitle getSubtitle() {
        return subtitle;
    }

    /**
     * Subtitle of the certificate.
     * (Required)
     * 
     */
    public void setSubtitle(Subtitle subtitle) {
        this.subtitle = subtitle;
    }

    /**
     * Title of the certificate.
     * (Required)
     * 
     */
    public String getTitle() {
        return title;
    }

    /**
     * Title of the certificate.
     * (Required)
     * 
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Details about the issuer of the certificate.
     * (Required)
     * 
     */
    public Issuer getIssuer() {
        return issuer;
    }

    /**
     * Details about the issuer of the certificate.
     * (Required)
     * 
     */
    public void setIssuer(Issuer issuer) {
        this.issuer = issuer;
    }

    /**
     * Description of what the certificate represents. Usually one - three sentences long.
     * (Required)
     * 
     */
    public String getDescription() {
        return description;
    }

    /**
     * Description of what the certificate represents. Usually one - three sentences long.
     * (Required)
     * 
     */
    public void setDescription(String description) {
        this.description = description;
    }

}

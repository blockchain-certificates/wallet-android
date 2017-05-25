
package com.learningmachine.android.app.data.cert.v20;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * SignatureLine schema
 * <p>
 * An extension that allows issuers to add signature lines to the visual representation of the badge. This is not part of the cryptographic proof; it is for display purposes only.
 * 
 */
public class SignatureLineSchema {

    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("image")
    @Expose
    private Object image;
    /**
     * Job title of signer, http://schema.org/jobTitle
     * 
     */
    @SerializedName("jobTitle")
    @Expose
    private String jobTitle;
    /**
     * Full name of signer, http://schema.org/name
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
    public Object getImage() {
        return image;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setImage(Object image) {
        this.image = image;
    }

    /**
     * Job title of signer, http://schema.org/jobTitle
     * 
     */
    public String getJobTitle() {
        return jobTitle;
    }

    /**
     * Job title of signer, http://schema.org/jobTitle
     * 
     */
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    /**
     * Full name of signer, http://schema.org/name
     * 
     */
    public String getName() {
        return name;
    }

    /**
     * Full name of signer, http://schema.org/name
     * 
     */
    public void setName(String name) {
        this.name = name;
    }

}

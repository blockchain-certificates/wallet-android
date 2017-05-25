
package com.learningmachine.android.app.data.cert.v11;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.net.URI;

public class Assertion {

    /**
     * Text, uri, etc. that shows evidence of the recipient's learning that the certificate represents. Can be left as an empty string if not used.
     * (Required)
     * 
     */
    @SerializedName("evidence")
    @Expose
    private String evidence;
    /**
     * Unique identifier. By default it is created using the string of a BSON ObjectId(), yielding an identifier 24 characters long.
     * (Required)
     * 
     */
    @SerializedName("uid")
    @Expose
    private String uid;
    /**
     * Date the the certificate JSON was created.
     * (Required)
     * 
     */
    @SerializedName("issuedOn")
    @Expose
    private DateTime issuedOn;
    /**
     * URI that links to the certificate on the viewer. Default is https://[domain]/[uid]
     * (Required)
     * 
     */
    @SerializedName("id")
    @Expose
    private URI id;
    /**
     * A base-64 encoded png image of the issuer's signature.
     * (Required)
     * 
     */
    @SerializedName("image:signature")
    @Expose
    private String imageSignature;

    /**
     * Text, uri, etc. that shows evidence of the recipient's learning that the certificate represents. Can be left as an empty string if not used.
     * (Required)
     * 
     */
    public String getEvidence() {
        return evidence;
    }

    /**
     * Text, uri, etc. that shows evidence of the recipient's learning that the certificate represents. Can be left as an empty string if not used.
     * (Required)
     * 
     */
    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }

    /**
     * Unique identifier. By default it is created using the string of a BSON ObjectId(), yielding an identifier 24 characters long.
     * (Required)
     * 
     */
    public String getUid() {
        return uid;
    }

    /**
     * Unique identifier. By default it is created using the string of a BSON ObjectId(), yielding an identifier 24 characters long.
     * (Required)
     * 
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Date the the certificate JSON was created.
     * (Required)
     * 
     */
    public DateTime getIssuedOn() {
        return issuedOn;
    }

    /**
     * Date the the certificate JSON was created.
     * (Required)
     * 
     */
    public void setIssuedOn(DateTime issuedOn) {
        this.issuedOn = issuedOn;
    }

    /**
     * URI that links to the certificate on the viewer. Default is https://[domain]/[uid]
     * (Required)
     * 
     */
    public URI getId() {
        return id;
    }

    /**
     * URI that links to the certificate on the viewer. Default is https://[domain]/[uid]
     * (Required)
     * 
     */
    public void setId(URI id) {
        this.id = id;
    }

    /**
     * A base-64 encoded png image of the issuer's signature.
     * (Required)
     * 
     */
    public String getImageSignature() {
        return imageSignature;
    }

    /**
     * A base-64 encoded png image of the issuer's signature.
     * (Required)
     * 
     */
    public void setImageSignature(String imageSignature) {
        this.imageSignature = imageSignature;
    }

}

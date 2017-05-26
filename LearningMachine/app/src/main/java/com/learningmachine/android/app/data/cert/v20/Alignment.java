
package com.learningmachine.android.app.data.cert.v20;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.net.URI;


/**
 * From https://w3id.org/openbadges#AlignmentObject
 * 
 */
class Alignment {

    /**
     * Defined by `targetName` property of https://w3id.org/openbadges#AlignmentObject
     * (Required)
     * 
     */
    @SerializedName("targetName")
    @Expose
    private String targetName;
    /**
     * Defined by `targetUrl` property of https://w3id.org/openbadges#AlignmentObject
     * (Required)
     * 
     */
    @SerializedName("targetUrl")
    @Expose
    private URI targetUrl;
    /**
     * Defined by `targetDescription` property of https://w3id.org/openbadges#AlignmentObject
     * 
     */
    @SerializedName("targetDescription")
    @Expose
    private String targetDescription;

    /**
     * Defined by `targetName` property of https://w3id.org/openbadges#AlignmentObject
     * (Required)
     * 
     */
    public String getTargetName() {
        return targetName;
    }

    /**
     * Defined by `targetName` property of https://w3id.org/openbadges#AlignmentObject
     * (Required)
     * 
     */
    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    /**
     * Defined by `targetUrl` property of https://w3id.org/openbadges#AlignmentObject
     * (Required)
     * 
     */
    public URI getTargetUrl() {
        return targetUrl;
    }

    /**
     * Defined by `targetUrl` property of https://w3id.org/openbadges#AlignmentObject
     * (Required)
     * 
     */
    public void setTargetUrl(URI targetUrl) {
        this.targetUrl = targetUrl;
    }

    /**
     * Defined by `targetDescription` property of https://w3id.org/openbadges#AlignmentObject
     * 
     */
    public String getTargetDescription() {
        return targetDescription;
    }

    /**
     * Defined by `targetDescription` property of https://w3id.org/openbadges#AlignmentObject
     * 
     */
    public void setTargetDescription(String targetDescription) {
        this.targetDescription = targetDescription;
    }

}

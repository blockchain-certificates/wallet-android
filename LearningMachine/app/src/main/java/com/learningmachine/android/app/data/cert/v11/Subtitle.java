
package com.learningmachine.android.app.data.cert.v11;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Subtitle of the certificate.
 * 
 */
public class Subtitle {

    /**
     * Content of the subtitle.
     * (Required)
     * 
     */
    @SerializedName("content")
    @Expose
    private String content;
    /**
     * Flag that indicates whether to show or hide the subtitle in the viewer. Backcompatible change to allow 2 types that occurred in the wild before proper validation.
     * (Required)
     * 
     */
    @SerializedName("display")
    @Expose
    private Object display;

    /**
     * Content of the subtitle.
     * (Required)
     * 
     */
    public String getContent() {
        return content;
    }

    /**
     * Content of the subtitle.
     * (Required)
     * 
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Flag that indicates whether to show or hide the subtitle in the viewer. Backcompatible change to allow 2 types that occurred in the wild before proper validation.
     * (Required)
     * 
     */
    public Object getDisplay() {
        return display;
    }

    /**
     * Flag that indicates whether to show or hide the subtitle in the viewer. Backcompatible change to allow 2 types that occurred in the wild before proper validation.
     * (Required)
     * 
     */
    public void setDisplay(Object display) {
        this.display = display;
    }

}

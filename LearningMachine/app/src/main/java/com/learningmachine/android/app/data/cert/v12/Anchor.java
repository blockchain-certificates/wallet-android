
package com.learningmachine.android.app.data.cert.v12;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class Anchor {

    /**
     * type of anchor, e.g. BTCOpReturn. Currently the only supported value is BTCOpReturn.
     * 
     */
    @SerializedName("type")
    @Expose
    private String type;
    /**
     * How to lookup the proof on the blockchain. Currently this is expected to be the (value of the) Bitcoin transaction id, and this value format is validated accordingly
     * (Required)
     * 
     */
    @SerializedName("sourceId")
    @Expose
    private String sourceId;

    /**
     * type of anchor, e.g. BTCOpReturn. Currently the only supported value is BTCOpReturn.
     * 
     */
    public String getType() {
        return type;
    }

    /**
     * type of anchor, e.g. BTCOpReturn. Currently the only supported value is BTCOpReturn.
     * 
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * How to lookup the proof on the blockchain. Currently this is expected to be the (value of the) Bitcoin transaction id, and this value format is validated accordingly
     * (Required)
     * 
     */
    public String getSourceId() {
        return sourceId;
    }

    /**
     * How to lookup the proof on the blockchain. Currently this is expected to be the (value of the) Bitcoin transaction id, and this value format is validated accordingly
     * (Required)
     * 
     */
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

}

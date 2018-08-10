
package com.learningmachine.android.app.data.cert.v20;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Anchor {

    public enum ChainType {
        bitcoin,
        testnet,
        regtest,
        mocknet,
        ethmain,
        unknown
    }

    @SerializedName("chain")
    @Expose
    private String chain;

    @SerializedName("sourceId")
    @Expose
    private String sourceId;

    @SerializedName("type")
    @Expose
    private String type;

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChain() {
        return chain;
    }

    public void setChain(String chain) {
        this.chain = chain;
    }

    public static boolean isValidChain(String chainName) {
        for (ChainType chainType :
                ChainType.values()) {
            if (chainType.name().equals(chainName)) {
                return true;
            }
        }
        return false;
    }

}


package com.learningmachine.android.app.data.cert.v11;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IssuerSchemaV11 {

    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("issuer_key")
    @Expose
    private List<IssuerKey> issuerKey = new ArrayList<IssuerKey>();
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("revocation_key")
    @Expose
    private List<RevocationKey> revocationKey = new ArrayList<RevocationKey>();

    /**
     * 
     * (Required)
     * 
     */
    public List<IssuerKey> getIssuerKey() {
        return issuerKey;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setIssuerKey(List<IssuerKey> issuerKey) {
        this.issuerKey = issuerKey;
    }

    /**
     * 
     * (Required)
     * 
     */
    public List<RevocationKey> getRevocationKey() {
        return revocationKey;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setRevocationKey(List<RevocationKey> revocationKey) {
        this.revocationKey = revocationKey;
    }

}

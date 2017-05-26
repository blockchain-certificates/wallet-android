
package com.learningmachine.android.app.data.cert.v20;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * MerkleProof2017 schema
 * <p>
 * An extension that allows an issuer to issue an Open Badge on the blockchain and provide proof of inclusion in a blockchain transaction. This uses the Chainpoint v2.0 specification: https://chainpoint.org/
 * 
 */
public class MerkleProof2017Schema {

    /**
     * A type or an array of types defined in a JSON-LD context file.
     * (Required)
     * 
     */
    @SerializedName("type")
    @Expose
    private Object type;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("merkleRoot")
    @Expose
    private String merkleRoot;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("targetHash")
    @Expose
    private String targetHash;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("proof")
    @Expose
    private List<Proof> proof = new ArrayList<Proof>();
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("anchors")
    @Expose
    private List<Anchor> anchors = new ArrayList<Anchor>();

    /**
     * A type or an array of types defined in a JSON-LD context file.
     * (Required)
     * 
     */
    public Object getType() {
        return type;
    }

    /**
     * A type or an array of types defined in a JSON-LD context file.
     * (Required)
     * 
     */
    public void setType(Object type) {
        this.type = type;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getMerkleRoot() {
        return merkleRoot;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setMerkleRoot(String merkleRoot) {
        this.merkleRoot = merkleRoot;
    }

    /**
     * 
     * (Required)
     * 
     */
    public String getTargetHash() {
        return targetHash;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setTargetHash(String targetHash) {
        this.targetHash = targetHash;
    }

    /**
     * 
     * (Required)
     * 
     */
    public List<Proof> getProof() {
        return proof;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setProof(List<Proof> proof) {
        this.proof = proof;
    }

    /**
     * 
     * (Required)
     * 
     */
    public List<Anchor> getAnchors() {
        return anchors;
    }

    /**
     * 
     * (Required)
     * 
     */
    public void setAnchors(List<Anchor> anchors) {
        this.anchors = anchors;
    }

}

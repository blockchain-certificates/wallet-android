
package com.learningmachine.android.app.data.cert.v12;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Blockchain Certificates Receipt Schema, Version 1.2
 * <p>
 * Provides evidence of the certificate on the blockchain, using the chainpoint v2 standard
 * 
 */
class Receipt {

    /**
     * This should always be chainpoint v2 JSON LD context
     * (Required)
     * 
     */
    @SerializedName("@context")
    @Expose
    private String context;
    /**
     * type of hash. Currently the only supported hash type is SHA256, with chainpoint type ChainpointSHA256v2.
     * 
     */
    @SerializedName("type")
    @Expose
    private String type;
    /**
     * hash of item being verified. Currently the only supported hash type is SHA256, and the targetHash format is validated accordingly.
     * (Required)
     * 
     */
    @SerializedName("targetHash")
    @Expose
    private String targetHash;
    /**
     * Merkle root value -- this is anchored to the blockchain. Currently the only supported hash type is SHA256, and merkleRoot format is validated accordingly.
     * (Required)
     * 
     */
    @SerializedName("merkleRoot")
    @Expose
    private String merkleRoot;
    /**
     * how to walk the Merkle tree from the target item to the Merkle root
     * (Required)
     * 
     */
    @SerializedName("proof")
    @Expose
    private List<Proof> proof = new ArrayList<Proof>();
    /**
     * how the proof is anchored to the blockchain
     * (Required)
     * 
     */
    @SerializedName("anchors")
    @Expose
    private List<Anchor> anchors = new ArrayList<Anchor>();

    /**
     * This should always be chainpoint v2 JSON LD context
     * (Required)
     * 
     */
    public String getContext() {
        return context;
    }

    /**
     * This should always be chainpoint v2 JSON LD context
     * (Required)
     * 
     */
    public void setContext(String context) {
        this.context = context;
    }

    /**
     * type of hash. Currently the only supported hash type is SHA256, with chainpoint type ChainpointSHA256v2.
     * 
     */
    public String getType() {
        return type;
    }

    /**
     * type of hash. Currently the only supported hash type is SHA256, with chainpoint type ChainpointSHA256v2.
     * 
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * hash of item being verified. Currently the only supported hash type is SHA256, and the targetHash format is validated accordingly.
     * (Required)
     * 
     */
    public String getTargetHash() {
        return targetHash;
    }

    /**
     * hash of item being verified. Currently the only supported hash type is SHA256, and the targetHash format is validated accordingly.
     * (Required)
     * 
     */
    public void setTargetHash(String targetHash) {
        this.targetHash = targetHash;
    }

    /**
     * Merkle root value -- this is anchored to the blockchain. Currently the only supported hash type is SHA256, and merkleRoot format is validated accordingly.
     * (Required)
     * 
     */
    public String getMerkleRoot() {
        return merkleRoot;
    }

    /**
     * Merkle root value -- this is anchored to the blockchain. Currently the only supported hash type is SHA256, and merkleRoot format is validated accordingly.
     * (Required)
     * 
     */
    public void setMerkleRoot(String merkleRoot) {
        this.merkleRoot = merkleRoot;
    }

    /**
     * how to walk the Merkle tree from the target item to the Merkle root
     * (Required)
     * 
     */
    public List<Proof> getProof() {
        return proof;
    }

    /**
     * how to walk the Merkle tree from the target item to the Merkle root
     * (Required)
     * 
     */
    public void setProof(List<Proof> proof) {
        this.proof = proof;
    }

    /**
     * how the proof is anchored to the blockchain
     * (Required)
     * 
     */
    public List<Anchor> getAnchors() {
        return anchors;
    }

    /**
     * how the proof is anchored to the blockchain
     * (Required)
     * 
     */
    public void setAnchors(List<Anchor> anchors) {
        this.anchors = anchors;
    }

}

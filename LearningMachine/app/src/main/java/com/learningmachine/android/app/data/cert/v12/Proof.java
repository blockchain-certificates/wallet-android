
package com.learningmachine.android.app.data.cert.v12;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class Proof {

    /**
     * value of left neighbor to combine into parent hash. Currently the only supported hash type is SHA256, and this value format is validated accordingly.
     * 
     */
    @SerializedName("left")
    @Expose
    private String left;
    /**
     * value of right neighbor to combine into parent hash. Currently the only supported hash type is SHA256, and this value format is validated accordingly.
     * 
     */
    @SerializedName("right")
    @Expose
    private String right;

    /**
     * value of left neighbor to combine into parent hash. Currently the only supported hash type is SHA256, and this value format is validated accordingly.
     * 
     */
    public String getLeft() {
        return left;
    }

    /**
     * value of left neighbor to combine into parent hash. Currently the only supported hash type is SHA256, and this value format is validated accordingly.
     * 
     */
    public void setLeft(String left) {
        this.left = left;
    }

    /**
     * value of right neighbor to combine into parent hash. Currently the only supported hash type is SHA256, and this value format is validated accordingly.
     * 
     */
    public String getRight() {
        return right;
    }

    /**
     * value of right neighbor to combine into parent hash. Currently the only supported hash type is SHA256, and this value format is validated accordingly.
     * 
     */
    public void setRight(String right) {
        this.right = right;
    }

}

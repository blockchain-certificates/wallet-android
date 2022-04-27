package com.learningmachine.android.app.data.model;

import com.google.gson.annotations.SerializedName;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Iterator;

public class DidDocument {
    @SerializedName("id")
    private String mId;
    @SerializedName("verificationMethod")
    private JsonArray mVerificationMethod;
    @SerializedName("service")
    private JsonArray mService;
    @SerializedName("authentication")
    private String[] mAuthentication;

    public String getIssuerProfile () {
        // return mService.find(entry => entry.type === "IssuerProfile");
        return "https://www.blockcerts.org/samples/3.0/issuer-blockcerts.json";
    }
}
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

    public String getIssuerProfileUrl () {
        Iterator iterator = mService.iterator();
        String issuerProfileUrl = "";
        while (iterator.hasNext()) {
            JsonObject serviceEntry = (JsonObject) iterator.next();
            if (serviceEntry.get("type").getAsString().equals("IssuerProfile")) {
                issuerProfileUrl = serviceEntry.get("serviceEndpoint").getAsString();
            }
            break;
        }
        return issuerProfileUrl;
    }
}
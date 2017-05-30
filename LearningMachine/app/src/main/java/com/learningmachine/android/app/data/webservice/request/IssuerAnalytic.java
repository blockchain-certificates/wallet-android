package com.learningmachine.android.app.data.webservice.request;

import com.google.gson.annotations.SerializedName;

public class IssuerAnalytic {

    @SerializedName("key")
    private String mKey;
    @SerializedName("action")
    private Action mAction;
    @SerializedName("metadata")
    private Metadata mMetadata;

    public IssuerAnalytic(String key, Action action) {
        mKey = key;
        mAction = action;
        mMetadata = new Metadata("42" ,"Android");
    }

    private class Metadata {
        @SerializedName("application")
        private String mApplicationString;
        @SerializedName("platform")
        private String mPlatformString;

        public Metadata(String applicationString, String platformString) {
            mApplicationString = applicationString;
            mPlatformString = platformString;
        }
    }

    public enum Action {
        @SerializedName("viewed")
        VIEWED,
        @SerializedName("verified")
        VERIFIED,
        @SerializedName("shared")
        SHARED,
    }
}

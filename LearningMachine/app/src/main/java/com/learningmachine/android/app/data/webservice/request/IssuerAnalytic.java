package com.learningmachine.android.app.data.webservice.request;

import android.annotation.SuppressLint;
import android.os.Build;

import com.google.gson.annotations.SerializedName;
import com.learningmachine.android.app.BuildConfig;

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
        mMetadata = new Metadata();
    }

    private class Metadata {
        @SerializedName("application")
        private String mApplicationString;
        @SerializedName("platform")
        private String mPlatformString;

        Metadata() {
            mApplicationString = BuildConfig.VERSION_NAME;
            mPlatformString = currentVersion();
        }

        @SuppressLint("DefaultLocale")
        private String currentVersion() {
            double release = Double.parseDouble(Build.VERSION.RELEASE);
            int apiLevel = Build.VERSION.SDK_INT;
            return String.format("Android %1$f, API %2$d", release, apiLevel);
        }
    }

    public enum Action {
        @SerializedName("viewed")
        VIEWED, @SerializedName("verified")
        VERIFIED, @SerializedName("shared")
        SHARED,
    }
}

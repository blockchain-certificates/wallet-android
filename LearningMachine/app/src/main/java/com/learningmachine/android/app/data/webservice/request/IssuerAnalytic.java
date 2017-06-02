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
    @SerializedName("application")
    private String mApplicationString;
    @SerializedName("platform")
    private String mPlatformString;

    public IssuerAnalytic(String key, Action action) {
        mKey = key;
        mAction = action;
        mApplicationString = BuildConfig.VERSION_NAME;
        mPlatformString = currentVersion();
    }

    @SuppressLint("DefaultLocale")
    private String currentVersion() {
        String release = Build.VERSION.RELEASE;
        int apiLevel = Build.VERSION.SDK_INT;
        return String.format("Android %1$s, API %2$d", release, apiLevel);
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

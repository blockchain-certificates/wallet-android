package com.learningmachine.android.app.data.url;

public class LaunchData {

    private LaunchType mLaunchType;

    private String mIntroUrl;
    private String mNonce;

    private String mCertUrl;

    public LaunchData(LaunchType launchType) {
        mLaunchType = launchType;
    }

    public LaunchData(LaunchType launchType, String introUrl, String nonce) {
        mLaunchType = launchType;
        mIntroUrl = introUrl;
        mNonce = nonce;
    }

    public LaunchData(LaunchType launchType, String certUrl) {
        mLaunchType = launchType;
        mCertUrl = certUrl;
    }

    public LaunchType getLaunchType() {
        return mLaunchType;
    }

    public String getIntroUrl() {
        return mIntroUrl;
    }

    public String getNonce() {
        return mNonce;
    }

    public String getCertUrl() {
        return mCertUrl;
    }
}

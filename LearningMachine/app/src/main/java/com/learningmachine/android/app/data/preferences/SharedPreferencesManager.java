package com.learningmachine.android.app.data.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    private static final String PREF_NAME = "LearningMachine";
    private static final String PREF_FIRST_LAUNCH = "SharedPreferencesManager.FirstLaunch";
    private static final String PREF_RETURN_USER = "SharedPreferencesManager.ReturnUser";
    private static final String PREF_SEEN_BACKUP_PASSPHRASE= "SharedPreferencesManager.SeenBackupPassphrase";
    private static final String PREF_LEGACY_RECEIVE_ADDRESS = "SharedPreferencesManager.LegacyReceiveAddress";

    private static final String DELAYED_ISSUER_URL = "SharedPreferencesManager.DelayedIssuer.URL";
    private static final String DELAYED_ISSUER_NONCE = "SharedPreferencesManager.DelayedIssuer.Nonce";

    private static final String DELAYED_CERTIFICATE_URL = "SharedPreferencesManager.DelayedCertificate.URL";

    private SharedPreferences mPrefs;

    public SharedPreferencesManager(Context context) {
        mPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public String getDelayedIssuerURL() {
        return mPrefs.getString(DELAYED_ISSUER_URL, "");
    }
    public String getDelayedIssuerNonce() {
        return mPrefs.getString(DELAYED_ISSUER_NONCE, "");
    }

    public void setDelayedIssuerURL(String issuerURL, String issuerNonce) {
        mPrefs.edit()
                .putString(DELAYED_ISSUER_URL, issuerURL)
                .putString(DELAYED_ISSUER_NONCE, issuerNonce)
                .apply();
    }

    public String getDelayedCertificateURL() {
        return mPrefs.getString(DELAYED_CERTIFICATE_URL, "");
    }

    public void setDelayedCertificateURL(String certificateURL) {
        mPrefs.edit()
                .putString(DELAYED_CERTIFICATE_URL, certificateURL)
                .apply();
    }

    public boolean isFirstLaunch() {
        return mPrefs.getBoolean(PREF_FIRST_LAUNCH, true);
    }

    public void setFirstLaunch(boolean firstLaunch) {
        mPrefs.edit()
                .putBoolean(PREF_FIRST_LAUNCH, firstLaunch)
                .apply();
    }

    public boolean shouldShowWelcomeBackUserFlow() {
        // if this is not a first time user and we have not stored the special preference key
        return isFirstLaunch() == false && mPrefs.contains(PREF_SEEN_BACKUP_PASSPHRASE) == false;
    }

    public boolean hasSeenBackupPassphraseBefore() {
        return mPrefs.getBoolean(PREF_SEEN_BACKUP_PASSPHRASE, false);
    }

    public void setHasSeenBackupPassphraseBefore(boolean hasSeenBackupPassphraseBefore) {
        mPrefs.edit()
                .putBoolean(PREF_SEEN_BACKUP_PASSPHRASE, hasSeenBackupPassphraseBefore)
                .apply();
    }

    public boolean wasReturnUser() {
        return mPrefs.getBoolean(PREF_RETURN_USER, true);
    }

    public void setWasReturnUser(boolean returnUser) {
        mPrefs.edit()
                .putBoolean(PREF_RETURN_USER, returnUser)
                .apply();
    }

    public String getLegacyReceiveAddress() {
        return mPrefs.getString(PREF_LEGACY_RECEIVE_ADDRESS, null);
    }

    public void setLegacyReceiveAddress(String receiveAddress) {
        mPrefs.edit()
                .putString(PREF_LEGACY_RECEIVE_ADDRESS, receiveAddress)
                .apply();
    }
}

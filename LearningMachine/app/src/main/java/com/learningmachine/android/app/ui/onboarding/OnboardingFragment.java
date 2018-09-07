package com.learningmachine.android.app.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.data.preferences.SharedPreferencesManager;
import com.learningmachine.android.app.ui.LMFragment;
import com.learningmachine.android.app.ui.home.HomeActivity;

import javax.inject.Inject;

public class OnboardingFragment extends LMFragment {

    @Inject SharedPreferencesManager mSharedPreferencesManager;

    public void onUserVisible() {

    }

    public boolean isBackAllowed() {
        return true;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.obtain(getContext())
                .inject(this);
    }

    public void didFindSavedPassphrase(String passphrase) {

    }

    public void didSavePassphraseToDevice(String passphrase) {

    }

    public boolean continueDelayedURLsFromDeepLinking() {
        if (mSharedPreferencesManager.getDelayedCertificateURL().length() > 0) {
            startActivityAndFinish(HomeActivity.newIntentForCert(getContext(),
                    mSharedPreferencesManager.getDelayedCertificateURL()));
            mSharedPreferencesManager.setDelayedCertificateURL("");
            return true;
        }
        if (mSharedPreferencesManager.getDelayedIssuerURL().length() > 0) {
            startActivityAndFinish(HomeActivity.newIntentForIssuer(getContext(),
                    mSharedPreferencesManager.getDelayedIssuerURL(),
                    mSharedPreferencesManager.getDelayedIssuerNonce()));
            mSharedPreferencesManager.setDelayedIssuerURL("", "");
            return true;
        }
        return false;
    }

    private void startActivityAndFinish(Intent intent) {
        startActivity(intent);
        getActivity().finish();
    }
}

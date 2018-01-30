package com.learningmachine.android.app.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.data.preferences.SharedPreferencesManager;
import com.learningmachine.android.app.ui.LMFragment;
import com.learningmachine.android.app.ui.cert.AddCertificateActivity;
import com.learningmachine.android.app.ui.issuer.AddIssuerActivity;

import java.util.ArrayList;

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

    public void checkForDelayedURLsFromDeepLinking() {
        if (mSharedPreferencesManager.getDelayedCertificateURL().length() > 0) {
            displayAlert(0,
                    R.string.onboarding_no_account,
                    R.string.wallet_does_not_exist_and_want_to_add_certificate,
                    R.string.onboarding_passphrase_ok,
                    R.string.onboarding_passphrase_cancel);
        }
        if (mSharedPreferencesManager.getDelayedIssuerURL().length() > 0) {
            displayAlert(0,
                    R.string.onboarding_no_account,
                    R.string.wallet_does_not_exist_and_want_to_add_issuer,
                    R.string.onboarding_passphrase_ok,
                    R.string.onboarding_passphrase_cancel);
        }
    }

    public boolean continueDelayedURLsFromDeepLinking() {
        if (mSharedPreferencesManager.getDelayedCertificateURL().length() > 0) {
            startActivityAndFinish(AddCertificateActivity.newIntent(getContext(), mSharedPreferencesManager.getDelayedCertificateURL()));
            mSharedPreferencesManager.setDelayedCertificateURL("");
            return true;
        }
        if (mSharedPreferencesManager.getDelayedIssuerURL().length() > 0) {
            startActivityAndFinish(AddIssuerActivity.newIntent(getContext(),
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

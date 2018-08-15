package com.learningmachine.android.app.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.data.preferences.SharedPreferencesManager;
import com.learningmachine.android.app.ui.LMFragment;
import com.learningmachine.android.app.ui.cert.AddCertificateActivity;
import com.learningmachine.android.app.ui.home.HomeActivity;
import com.learningmachine.android.app.util.DialogUtils;

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
            DialogUtils.showAlertDialog(getContext(), this,
                    R.drawable.ic_dialog_failure,
                    getResources().getString(R.string.onboarding_no_account),
                    getResources().getString(R.string.wallet_does_not_exist_and_want_to_add_certificate),
                    null,
                    getResources().getString(R.string.onboarding_passphrase_ok),
                    (btnIdx) -> {
                        return null;
                    });
        }
        if (mSharedPreferencesManager.getDelayedIssuerURL().length() > 0) {
            DialogUtils.showAlertDialog(getContext(), this,
                    R.drawable.ic_dialog_failure,
                    getResources().getString(R.string.onboarding_no_account),
                    getResources().getString(R.string.wallet_does_not_exist_and_want_to_add_issuer),
                    null,
                    getResources().getString(R.string.onboarding_passphrase_ok),
                    (btnIdx) -> {
                        return null;
                    });
        }
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

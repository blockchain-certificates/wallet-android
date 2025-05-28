package com.hyland.android.app.ui.splash;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;

import com.hyland.android.app.R;
import com.hyland.android.app.data.bitcoin.BitcoinManager;
import com.hyland.android.app.data.inject.Injector;
import com.hyland.android.app.data.passphrase.PassphraseManager;
import com.hyland.android.app.data.preferences.SharedPreferencesManager;
import com.hyland.android.app.data.url.LaunchData;
import com.hyland.android.app.data.url.SplashUrlDecoder;
import com.hyland.android.app.dialog.AlertDialogFragment;
import com.hyland.android.app.ui.LMActivity;
import com.hyland.android.app.ui.home.HomeActivity;
import com.hyland.android.app.ui.onboarding.OnboardingActivity;

import javax.inject.Inject;

import timber.log.Timber;

import static com.hyland.android.app.data.url.LaunchType.ADD_CERTIFICATE;
import static com.hyland.android.app.data.url.LaunchType.ADD_ISSUER;

public class SplashActivity extends LMActivity {

    @Inject protected SharedPreferencesManager mSharedPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.obtain(this)
                .inject(this);

        launch();
    }

    private void launch() {
        Intent intent = getIntent();
        Uri data = intent.getData();
        String uriString = (data == null) ? null : data.toString();

        LaunchData launchData = SplashUrlDecoder.getLaunchType(uriString);

        // Note: If we have not "logged into" an account yet, then we need to force the user into onboarding
        if(mSharedPreferencesManager.isFirstLaunch() || mSharedPreferencesManager.shouldShowWelcomeBackUserFlow()) {
            if(launchData.getLaunchType() == ADD_ISSUER) {
                mSharedPreferencesManager.setDelayedIssuerURL(launchData.getIntroUrl(), launchData.getNonce());
            }
            if(launchData.getLaunchType() == ADD_CERTIFICATE) {
                mSharedPreferencesManager.setDelayedCertificateURL(launchData.getCertUrl());
            }
            startActivityAndFinish(new Intent(this, OnboardingActivity.class));
            return;
        }

        switch (launchData.getLaunchType()) {

            case ONBOARDING:
            case MAIN:
                Timber.i("Application was launched from a user activity.");
                startActivityAndFinish(new Intent(this, HomeActivity.class));
                break;

            case ADD_ISSUER:
                Timber.i("Application was launched with this url: " + data.toString());
                Intent issuerIntent = HomeActivity.newIntentForIssuer(this,
                        launchData.getIntroUrl(),
                        launchData.getNonce());
                issuerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                startActivityAndFinish(issuerIntent);
                break;

            case ADD_CERTIFICATE:
                Timber.i("Application was launched with this url: " + data.toString());
                Intent certificateIntent = HomeActivity.newIntentForCert(this, launchData.getCertUrl());
                certificateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                startActivityAndFinish(certificateIntent);
                break;
        }

    }

    private void startActivityAndFinish(Intent intent) {
        startActivity(intent);
        finish();
    }

}

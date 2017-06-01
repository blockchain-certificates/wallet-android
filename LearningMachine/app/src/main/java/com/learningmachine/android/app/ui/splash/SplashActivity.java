package com.learningmachine.android.app.ui.splash;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.learningmachine.android.app.data.url.LaunchData;
import com.learningmachine.android.app.data.url.SplashUrlDecoder;
import com.learningmachine.android.app.ui.LMActivity;
import com.learningmachine.android.app.ui.cert.AddCertificateActivity;
import com.learningmachine.android.app.ui.home.HomeActivity;
import com.learningmachine.android.app.ui.issuer.AddIssuerActivity;
import com.learningmachine.android.app.ui.onboarding.OnboardingActivity;

public class SplashActivity extends LMActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Uri data = intent.getData();
        String uriString = (data == null)? null : data.toString();

        LaunchData launchData = SplashUrlDecoder.getLaunchType(uriString);

        switch (launchData.getLaunchType()) {

            case MAIN:
                startActivityAndFinish(new Intent(this, HomeActivity.class));
                break;

            case ONBOARDING:
                startActivityAndFinish(new Intent(this, OnboardingActivity.class));
                break;

            case ADD_ISSUER:
                startActivityAndFinish(AddIssuerActivity.newIntent(this, launchData.getIntroUrl(), launchData.getNonce()));
                break;

            case ADD_CERTIFICATE:
                startActivityAndFinish(AddCertificateActivity.newIntent(this, launchData.getCertUrl()));
                break;
        }

    }

    private void startActivityAndFinish(Intent intent) {
        startActivity(intent);
        finish();
    }

}

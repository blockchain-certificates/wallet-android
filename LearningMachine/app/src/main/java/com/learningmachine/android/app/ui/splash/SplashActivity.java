package com.learningmachine.android.app.ui.splash;

import android.content.Intent;
import android.os.Bundle;

import com.learningmachine.android.app.ui.LMActivity;
import com.learningmachine.android.app.ui.onboarding.OnboardingActivity;

public class SplashActivity extends LMActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigateToNextActivity();
    }

    private void navigateToNextActivity() {
        startActivity(new Intent(SplashActivity.this, OnboardingActivity.class));
        finish();
    }
}

package com.learningmachine.android.app.ui.onboarding;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.data.preferences.SharedPreferencesManager;
import com.learningmachine.android.app.databinding.ActivityOnboardingBinding;
import com.learningmachine.android.app.ui.LMActivity;
import com.learningmachine.android.app.ui.onboarding.OnboardingFlow.FlowType;
import com.learningmachine.android.app.util.AESCrypt;

import java.io.File;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.util.Scanner;

import javax.inject.Inject;

public class OnboardingActivity extends LMActivity implements AccountChooserFragment.Callback {

    @Inject SharedPreferencesManager mSharedPreferencesManager;

    private static final String SAVED_FLOW = "onboardingFlow";

    private OnboardingFlow mOnboardingFlow;
    private OnboardingAdapter mAdapter;
    private ActivityOnboardingBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.obtain(this)
                .inject(this);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_onboarding);
        mBinding.onboardingViewPager.addOnPageChangeListener(mOnPageChangeListener);

        if (savedInstanceState == null) {
            if (mSharedPreferencesManager.shouldShowWelcomeBackUserFlow()) {
                mOnboardingFlow = new OnboardingFlow(FlowType.BACKUP_ONLY);
            } else {
                mOnboardingFlow = new OnboardingFlow(FlowType.UNKNOWN);
            }
        } else {
            mOnboardingFlow = (OnboardingFlow) savedInstanceState.getSerializable(SAVED_FLOW);
        }

        setupAdapter();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVED_FLOW, mOnboardingFlow);
    }

    @Override
    public void onBackPressed() {
        if (navigateBackward()) {
            return;
        }

        //This is the last screen in the app.
        //Tapping back should close the app.
        finishAffinity();
    }

    public void onContinuePastWelcomeScreen() {
        navigateForward();
    }

    public void onBackupPassphrase() {
        navigateForward();
    }

    @Override
    public void onNewAccount() {
        replaceScreens(FlowType.NEW_ACCOUNT);
    }

    @Override
    public void onExistingAccount() {
        replaceScreens(FlowType.EXISTING_ACCOUNT);
    }

    private void setupAdapter() {
        mAdapter = new OnboardingAdapter(getSupportFragmentManager(), mOnboardingFlow.getScreens());
        mBinding.onboardingViewPager.setAdapter(mAdapter);
        mBinding.onboardingViewPager.setCurrentItem(mOnboardingFlow.getPosition());
    }

    private void replaceScreens(FlowType flowType) {
        mOnboardingFlow = new OnboardingFlow(flowType);
        mOnboardingFlow.setPosition(1);

        mAdapter.setScreens(mOnboardingFlow.getScreens());
        mBinding.onboardingViewPager.getAdapter().notifyDataSetChanged();
        mBinding.onboardingViewPager.setCurrentItem(mOnboardingFlow.getPosition());
    }

    private void navigateForward() {
        int position = mOnboardingFlow.getPosition() + 1;
        mBinding.onboardingViewPager.setCurrentItem(position);
        mOnboardingFlow.setPosition(position);
    }

    private boolean navigateBackward() {
        int position = mOnboardingFlow.getPosition();
        if (position <= 0) {
            return false;
        }

        OnboardingFragment currentFragment = mAdapter.getCurrentFragment();
        if (currentFragment != null && !currentFragment.isBackAllowed()) {
            // disallow back if the fragment requests it
            return true;
        }

        mOnboardingFlow.setPosition(position - 1);
        mBinding.onboardingViewPager.setCurrentItem(mOnboardingFlow.getPosition());
        return true;
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) { }

        @Override
        public void onPageSelected(int position) {
            mOnboardingFlow.setPosition(position);

            OnboardingFragment currentFragment = mAdapter.getCurrentFragment();
            if (currentFragment != null) {
                currentFragment.onUserVisible();
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) { }
    };


    public boolean isOnAccountsScreen() {
        return mOnboardingFlow.getPosition() == 0;
    }


}

package com.learningmachine.android.app.ui.onboarding;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.databinding.ActivityOnboardingBinding;
import com.learningmachine.android.app.ui.LMActivity;
import com.learningmachine.android.app.ui.onboarding.OnboardingFlow.FlowType;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class OnboardingActivity extends LMActivity implements AccountChooserFragment.Callback,
    GeneratePassphraseFragment.Callback {

    private static final String SAVED_FLOW = "onboardingFlow";

    private OnboardingFlow mOnboardingFlow;
    private OnboardingAdapter mAdapter;
    private ActivityOnboardingBinding mBinding;

    private String tempPassphrase = null;
    private PastePassphraseFragment passphraseFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_onboarding);
        mBinding.onboardingViewPager.addOnPageChangeListener(mOnPageChangeListener);

        if (savedInstanceState == null) {
            mOnboardingFlow = new OnboardingFlow(FlowType.UNKNOWN);
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

        super.onBackPressed();
    }

    @Override
    public void onNewAccount() {
        replaceScreens(FlowType.NEW_ACCOUNT);
    }

    @Override
    public void onExistingAccount() {
        replaceScreens(FlowType.EXISTING_ACCOUNT);
    }

    @Override
    public void onGeneratePassphraseClick() {
        navigateForward();
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


    public static String pathToSavedPassphraseFile() {
        return Environment.getExternalStorageDirectory() + "/learningmachine.dat";
    }

    private void savePassphraseToDevice(String passphrase) {
        String passphraseFile = pathToSavedPassphraseFile();
        try( PrintWriter out = new PrintWriter(passphraseFile) ) {
            // TODO: encrypt the string
            String mneumonicString = EncryptionUtils.encrypt();

            out.println("mneumonic:"+passphrase);
        } catch(Exception e) {
            // note: we don't truly care if this fails. this feature is a nice to have, and if it works
            // then the user gets to enjoy their passphrase magically showing up again if they
            // uninstall and re-install the app.
        }
    }

    public void askToSavePassphraseToDevice(String passphrase) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                savePassphraseToDevice(passphrase);
            } else {
                tempPassphrase = passphrase;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        } else {
            savePassphraseToDevice(passphrase);
        }
    }

    public void askToGetPassphraseFromDevice(PastePassphraseFragment fragment) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getSavedPassphraseFromDevice(fragment);
            } else {
                passphraseFragment = fragment;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        } else {
            getSavedPassphraseFromDevice(fragment);
        }
    }

    private void getSavedPassphraseFromDevice(PastePassphraseFragment fragment) {
        String passphraseFile = OnboardingActivity.pathToSavedPassphraseFile();
        try {
            String content = new Scanner(new File(passphraseFile)).useDelimiter("\\Z").next();
            // TODO: decrypt to file
            if (content.startsWith("mneumonic:")) {
                fragment.didFindSavedPassphrase(content.substring(10).trim());
            }
        } catch(Exception e) {
            // note: this is a non-critical feature, so if this fails nbd
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(tempPassphrase != null) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                savePassphraseToDevice(tempPassphrase);
            }
            tempPassphrase = null;
        }

        if(passphraseFragment != null) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getSavedPassphraseFromDevice(passphraseFragment);
            }
            passphraseFragment = null;
        }
    }

}

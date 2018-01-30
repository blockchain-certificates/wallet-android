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
import com.learningmachine.android.app.databinding.ActivityOnboardingBinding;
import com.learningmachine.android.app.ui.LMActivity;
import com.learningmachine.android.app.ui.onboarding.OnboardingFlow.FlowType;
import com.learningmachine.android.app.util.AESCrypt;

import java.io.File;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.util.Scanner;

public class OnboardingActivity extends LMActivity implements AccountChooserFragment.Callback {

    private static final String SAVED_FLOW = "onboardingFlow";

    private OnboardingFlow mOnboardingFlow;
    private OnboardingAdapter mAdapter;
    private ActivityOnboardingBinding mBinding;

    private String tempPassphrase = null;
    private OnboardingFragment passphraseFragment = null;

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

    private String getDeviceId(Context context) {
        final String deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        if(deviceId == null || deviceId.length() == 0) {
            return "NOT_IDEAL_KEY";
        }
        return deviceId;
    }

    private void savePassphraseToDevice(String passphrase, OnboardingFragment fragment) {
        if (passphrase == null) {
            fragment.didSavePassphraseToDevice(null);
            return;
        }

        String passphraseFile = pathToSavedPassphraseFile();
        try( PrintWriter out = new PrintWriter(passphraseFile) ) {
            String encryptionKey= getDeviceId(getApplicationContext());
            String mneumonicString = "mneumonic:"+passphrase;
            try {
                String encryptedMsg = AESCrypt.encrypt(encryptionKey, mneumonicString);
                out.println(encryptedMsg);
                fragment.didSavePassphraseToDevice(passphrase);
            }catch (GeneralSecurityException e){
                fragment.didSavePassphraseToDevice(null);
            }
        } catch(Exception e) {
            fragment.didSavePassphraseToDevice(null);
        }
    }

    public void askToSavePassphraseToDevice(String passphrase, OnboardingFragment fragment) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                savePassphraseToDevice(passphrase, fragment);
            } else {
                tempPassphrase = passphrase;
                passphraseFragment = fragment;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        } else {
            savePassphraseToDevice(passphrase, fragment);
        }
    }

    public void askToGetPassphraseFromDevice(OnboardingFragment fragment) {

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getSavedPassphraseFromDevice(fragment);
            } else {
                passphraseFragment = fragment;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        } else {
            getSavedPassphraseFromDevice(fragment);
        }
    }

    private boolean getSavedPassphraseFromDevice(OnboardingFragment fragment) {
        String passphraseFile = OnboardingActivity.pathToSavedPassphraseFile();
        try {
            String encryptedMsg = new Scanner(new File(passphraseFile)).useDelimiter("\\Z").next();
            String encryptionKey = getDeviceId(getApplicationContext());
            try {
                String content = AESCrypt.decrypt(encryptionKey, encryptedMsg);
                if (content.startsWith("mneumonic:")) {
                    fragment.didFindSavedPassphrase(content.substring(10).trim());
                    return true;
                }
            }catch (GeneralSecurityException e){

            }
        } catch(Exception e) {
            // note: this is a non-critical feature, so if this fails nbd
        }

        fragment.didFindSavedPassphrase(null);
        return false;
    }



    private boolean didReceivePermissionsCallback = false;
    private boolean didSucceedInPermissionsRequest = false;
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Note: this really sucks, but android will crash if we try and display dialogs in the permissions
        // result callback.  So we delay this until onResume is called on the activity
        didReceivePermissionsCallback = true;
        didSucceedInPermissionsRequest = grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

    protected void onResume() {
        super.onResume();


        if(didReceivePermissionsCallback){
            if(tempPassphrase != null && passphraseFragment != null) {
                if (didSucceedInPermissionsRequest) {
                    savePassphraseToDevice(tempPassphrase, passphraseFragment);
                } else {
                    savePassphraseToDevice(null, passphraseFragment);
                }
                tempPassphrase = null;
                passphraseFragment = null;
            }

            if(passphraseFragment != null) {
                if(didSucceedInPermissionsRequest){
                    getSavedPassphraseFromDevice(passphraseFragment);
                } else {
                    getSavedPassphraseFromDevice(null);
                }
                passphraseFragment = null;
            }

            didReceivePermissionsCallback = false;
            didSucceedInPermissionsRequest = false;
        }
    }

}

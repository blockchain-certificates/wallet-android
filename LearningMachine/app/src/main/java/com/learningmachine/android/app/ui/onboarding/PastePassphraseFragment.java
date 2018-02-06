package com.learningmachine.android.app.ui.onboarding;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.bitcoin.BitcoinManager;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.databinding.FragmentPastePassphraseBinding;
import com.learningmachine.android.app.ui.home.HomeActivity;
import com.learningmachine.android.app.util.DialogUtils;
import com.learningmachine.android.app.util.StringUtils;

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

public class PastePassphraseFragment extends OnboardingFragment {

    @Inject protected BitcoinManager mBitcoinManager;

    private FragmentPastePassphraseBinding mBinding;


    private Timer countingTimer;
    private int countingSeconds = 1;

    public void startCountingTimer() {
        if(countingTimer != null) {
            return;
        }

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBinding.passphraseLabel.setText(getResources().getString(R.string.onboarding_paste_passphrase_load_1) + " " + countingSeconds + "s");
                        countingSeconds++;
                    }
                });
            }
        };

        countingTimer = new Timer();
        countingTimer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    public void stopCountingTimer() {
        countingTimer.cancel();
        countingTimer = null;
    }


    public static PastePassphraseFragment newInstance() {
        return new PastePassphraseFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.obtain(getContext())
                .inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_paste_passphrase, container, false);

        mBinding.passphraseLabel.setText(R.string.onboarding_paste_passphrase_load_0);

        ((OnboardingActivity)getActivity()).askToGetPassphraseFromDevice(this);

        mBinding.pastePassphraseEditText.addTextChangedListener(new PastePassphraseTextWatcher());
        mBinding.doneButton.setAlpha(0.3f);
        mBinding.doneButton.setEnabled(false);
        mBinding.doneButton.setOnClickListener(view -> onDone());

        return mBinding.getRoot();
    }

    @Override
    public void didFindSavedPassphrase(String passphrase) {
        if (passphrase != null) {
            mBinding.pastePassphraseEditText.setText(passphrase);
            onDone();
        } else {
            mBinding.passphraseLabel.setText(R.string.onboarding_paste_passphrase_load_2);
            mBinding.passphraseLabel.requestFocus();
        }
    }


    private void onDone() {

        String passphrase = mBinding.pastePassphraseEditText.getText().toString();
        Activity activity = getActivity();

        startCountingTimer();

        mBinding.doneButton.setAlpha(0.3f);
        mBinding.doneButton.setEnabled(false);
        mBinding.pastePassphraseEditText.setEnabled(false);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                mBitcoinManager.setPassphrase(passphrase)
                        .compose(bindToMainThread())
                        .subscribe(wallet -> {

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    stopCountingTimer();


                                    mSharedPreferencesManager.setFirstLaunch(false);
                                    if (continueDelayedURLsFromDeepLinking() == false) {
                                        startActivity(new Intent(getActivity(), HomeActivity.class));
                                        getActivity().finish();
                                    }
                                }
                            });
                        }, e -> displayErrorsLocal(e, DialogUtils.ErrorCategory.GENERIC, R.string.error_title_message));
            }
        });
    }


    protected void displayErrorsLocal(Throwable throwable, DialogUtils.ErrorCategory errorCategory, @StringRes int errorTitleResId) {
        stopCountingTimer();
        mBinding.passphraseLabel.setText(R.string.onboarding_paste_passphrase_load_2);
        mBinding.pastePassphraseEditText.setEnabled(true);
        mBinding.pastePassphraseEditText.setText("");
        displayErrors(throwable, errorCategory, errorTitleResId);
    }


    private class PastePassphraseTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String passphrase = mBinding.pastePassphraseEditText.getText()
                    .toString();
            boolean emptyPassphrase = StringUtils.isEmpty(passphrase);
            mBinding.doneButton.setEnabled(!emptyPassphrase);
            if (mBinding.doneButton.isEnabled()) {
                mBinding.doneButton.setAlpha(1.0f);
            } else {
                mBinding.doneButton.setAlpha(0.3f);
            }
        }
    }
}

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
import android.text.InputFilter;
import android.text.Spanned;
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
import com.learningmachine.android.app.data.preferences.SharedPreferencesManager;
import com.learningmachine.android.app.databinding.FragmentPastePassphraseBinding;
import com.learningmachine.android.app.ui.LMActivity;
import com.learningmachine.android.app.ui.home.HomeActivity;
import com.learningmachine.android.app.util.DialogUtils;
import com.learningmachine.android.app.util.StringUtils;

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import timber.log.Timber;

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
                Activity activity = getActivity();
                if(activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mBinding.passphraseLabel.setText(getResources().getString(R.string.onboarding_paste_passphrase_load_1) + " " + countingSeconds + "s");
                            countingSeconds++;
                        }
                    });
                }
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

        ((LMActivity)getActivity()).askToGetPassphraseFromDevice((passphrase) -> {
            if (passphrase != null) {
                mBinding.pastePassphraseEditText.setText(passphrase.toString());
                onDone();
            } else {
                mBinding.passphraseLabel.setText(R.string.onboarding_paste_passphrase_load_2);
                mBinding.passphraseLabel.requestFocus();
            }
            return null;
        });

        mBinding.pastePassphraseEditText.setFilters(new InputFilter[] {
                new InputFilter.AllCaps() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        String toLowered = String.valueOf(source).toLowerCase();
                        String sanitized = toLowered.replaceAll("[^a-zA-Z ]", "");
                        return sanitized;
                    }
                }
        });

        mBinding.pastePassphraseEditText.addTextChangedListener(new PastePassphraseTextWatcher());
        mBinding.doneButton.setEnabled(false);
        mBinding.doneButton.setOnClickListener(view -> onDone());

        return mBinding.getRoot();
    }


    private void onDone() {

        String passphrase = mBinding.pastePassphraseEditText.getText().toString();
        Activity activity = getActivity();

        startCountingTimer();

        mBinding.doneButton.setEnabled(false);
        mBinding.pastePassphraseEditText.setEnabled(false);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                mBitcoinManager.setPassphrase(passphrase)
                        .compose(bindToMainThread())
                        .subscribe(wallet -> {

                            stopCountingTimer();

                            if(isVisible()) {

                                Log.d("LM", "PastePassphraseFragment isVisible()");

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if(isVisible()) {
                                            // if we return to the app by pasting in our passphrase, we
                                            // must have already backed it up!
                                            mSharedPreferencesManager.setHasSeenBackupPassphraseBefore(true);
                                            mSharedPreferencesManager.setWasReturnUser(true);
                                            mSharedPreferencesManager.setFirstLaunch(false);
                                            if (continueDelayedURLsFromDeepLinking() == false) {
                                                startActivity(new Intent(getActivity(), HomeActivity.class));
                                                getActivity().finish();
                                            }
                                        }
                                    }
                                });
                            }
                        }, e -> {
                            Timber.e(e, "Could not set passphrase.");
                            displayErrorsLocal(e, DialogUtils.ErrorCategory.GENERIC, R.string.error_title_message);
                        });
            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    protected void displayErrorsLocal(Throwable throwable, DialogUtils.ErrorCategory errorCategory, @StringRes int errorTitleResId) {
        stopCountingTimer();

        mBinding.passphraseLabel.setText(R.string.onboarding_paste_passphrase_load_2);
        mBinding.pastePassphraseEditText.setEnabled(true);
        mBinding.pastePassphraseEditText.setText("");

        DialogUtils.showAlertDialog(getContext(), this,
                R.drawable.ic_dialog_failure,
                getResources().getString(R.string.cert_passphrase_invalid_title),
                getResources().getString(R.string.cert_passphrase_invalid_desc),
                null,
                getResources().getString(R.string.onboarding_passphrase_ok),
                (btnIdx) -> {
                    return null;
                });
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
        }
    }
}

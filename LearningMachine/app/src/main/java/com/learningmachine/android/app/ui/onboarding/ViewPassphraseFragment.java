package com.learningmachine.android.app.ui.onboarding;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.bitcoin.BitcoinManager;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.databinding.FragmentBackupPassphraseBinding;
import com.learningmachine.android.app.databinding.FragmentViewPassphraseBinding;
import com.learningmachine.android.app.dialog.AlertDialogFragment;
import com.learningmachine.android.app.ui.home.HomeActivity;
import com.learningmachine.android.app.util.DialogUtils;

import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import static android.content.ContentValues.TAG;

public class ViewPassphraseFragment extends OnboardingFragment {

    @Inject protected BitcoinManager mBitcoinManager;

    private FragmentViewPassphraseBinding mBinding;
    private String mPassphrase;

    private Timer countingTimer;
    private int countingSeconds = 1;
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    mBinding.onboardingStatusText.setText(getResources().getString(R.string.onboarding_passphrase_status_0) + " " + countingSeconds + "s");
                    countingSeconds++;
                }
            });
        }
    };

    public void startCountingTimer() {
        if(countingTimer != null) {
            return;
        }
        countingTimer = new Timer();
        countingTimer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    public void stopCountingTimer() {
        countingTimer.cancel();
        countingTimer = null;
    }

    public static ViewPassphraseFragment newInstance() {
        return new ViewPassphraseFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.obtain(getContext())
                .inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_passphrase, container, false);

        mBinding.onboardingStatusText.setText(R.string.onboarding_passphrase_status_0);
        startCountingTimer();

        mBinding.onboardingDoneButton.setOnClickListener(view -> onDone());
        mBinding.onboardingDoneButton.setAlpha(0.3f);
        mBinding.onboardingDoneButton.setEnabled(false);

        mBinding.onboardingPassphraseTitle.setVisibility(View.INVISIBLE);
        mBinding.onboardingPassphraseContent.setVisibility(View.INVISIBLE);

        return mBinding.getRoot();
    }

    @Override
    public void onUserVisible() {
        super.onUserVisible();

        Activity activity = getActivity();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                mBitcoinManager.getPassphrase().subscribe(passphrase -> {
                    mPassphrase = passphrase;

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stopCountingTimer();


                            mBinding.onboardingPassphraseTitle.setVisibility(View.VISIBLE);
                            mBinding.onboardingPassphraseContent.setVisibility(View.VISIBLE);

                            mBinding.onboardingStatusText.setText(R.string.onboarding_passphrase_status_1);
                            mBinding.onboardingPassphraseContent.setText(passphrase);

                            mBinding.onboardingDoneButton.setAlpha(1.0f);
                            mBinding.onboardingDoneButton.setEnabled(true);
                        }
                    });
                });
            }
        });

    }

    @Override
    public boolean isBackAllowed() {
        return false;
    }

    private void onDone() {
        ((OnboardingActivity)getActivity()).onBackupPassphrase();
    }


}

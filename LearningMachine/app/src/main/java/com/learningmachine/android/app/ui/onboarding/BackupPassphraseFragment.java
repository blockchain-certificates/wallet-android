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
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.bitcoin.BitcoinManager;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.databinding.FragmentBackupPassphraseBinding;
import com.learningmachine.android.app.databinding.FragmentViewPassphraseBinding;
import com.learningmachine.android.app.dialog.AlertDialogFragment;
import com.learningmachine.android.app.ui.home.HomeActivity;
import com.learningmachine.android.app.util.DialogUtils;
import com.smallplanet.labalib.Laba;

import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import static android.content.ContentValues.TAG;

public class BackupPassphraseFragment extends OnboardingFragment {

    @Inject protected BitcoinManager mBitcoinManager;

    protected FragmentBackupPassphraseBinding mBinding;
    protected String mPassphrase;

    private int numberOfBackupOptionsUsed = 0;

    public static BackupPassphraseFragment newInstance() {
        return new BackupPassphraseFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.obtain(getContext())
                .inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_backup_passphrase, container, false);

        mBinding.onboardingDoneButton.setOnClickListener(view -> onDone());
        mBinding.onboardingEmailButton.setOnClickListener(view -> onEmail());
        mBinding.onboardingSaveButton.setOnClickListener(view -> onSave());
        mBinding.onboardingWriteButton.setOnClickListener(view -> onWrite());

        Laba.Animate(mBinding.onboardingDoneButton, "d0v200", () -> { return null; });
        mBinding.onboardingDoneButton.setAlpha(0.3f);
        mBinding.onboardingDoneButton.setEnabled(false);

        mBinding.onboardingSaveCheckmark.setVisibility(View.INVISIBLE);
        mBinding.onboardingEmailCheckmark.setVisibility(View.INVISIBLE);
        mBinding.onboardingWriteCheckmark.setVisibility(View.INVISIBLE);

        return mBinding.getRoot();
    }

    @Override
    public void onUserVisible() {
        super.onUserVisible();

        mBitcoinManager.getPassphrase().subscribe(passphrase -> {
            mPassphrase = passphrase;
        });

    }

    @Override
    public boolean isBackAllowed() {
        return true;
    }

    private void onDone() {

        mSharedPreferencesManager.setHasSeenBackupPassphraseBefore(true);
        mSharedPreferencesManager.setWasReturnUser(false);
        mSharedPreferencesManager.setFirstLaunch(false);
        if (continueDelayedURLsFromDeepLinking() == false) {
            startActivity(new Intent(getActivity(), HomeActivity.class));
            getActivity().finish();
        }
    }

    protected void onSave() {
        ((OnboardingActivity)getActivity()).askToSavePassphraseToDevice(mPassphrase, (passphrase) -> {
            if(passphrase == null) {

                DialogUtils.showAlertDialog(getContext(), this,
                        R.drawable.ic_dialog_failure,
                        getResources().getString(R.string.onboarding_passphrase_permissions_error_title),
                        getResources().getString(R.string.onboarding_passphrase_permissions_error),
                        getResources().getString(R.string.onboarding_passphrase_ok),
                        null,
                        (btnIdx) -> {
                            HandleBackupOptionCompleted(null);
                            return null;
                        });
                return null;
            }

            DialogUtils.showAlertDialog(getContext(), this,
                    R.drawable.ic_dialog_success,
                    getResources().getString(R.string.onboarding_passphrase_complete_title),
                    getResources().getString(R.string.onboarding_passphrase_save_complete),
                    getResources().getString(R.string.onboarding_passphrase_ok),
                    null,
                    (btnIdx) -> {
                        if(mBinding != null) {
                            HandleBackupOptionCompleted(mBinding.onboardingSaveCheckmark);
                        }
                        return null;
                    });
            return null;
        });
    }

    protected void onEmail() {
        DialogUtils.showAlertDialog(getContext(), this,
                0,
                getResources().getString(R.string.onboarding_passphrase_email_before_title),
                getResources().getString(R.string.onboarding_passphrase_email_before),
                getResources().getString(R.string.onboarding_passphrase_cancel),
                getResources().getString(R.string.onboarding_passphrase_ok),
                (btnIdx) -> {

                    if((int)btnIdx == 0) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT, "BlockCerts Backup");
                        intent.putExtra(Intent.EXTRA_TEXT, mPassphrase);
                        Intent mailer = Intent.createChooser(intent, null);
                        startActivity(mailer);

                        if(mBinding != null) {
                            HandleBackupOptionCompleted(mBinding.onboardingEmailCheckmark);
                        }
                    }
                    return null;
                });
    }

    protected void onWrite() {
        AlertDialogFragment fragment = DialogUtils.showCustomDialog(getContext(), this,
                R.layout.dialog_write_passphrase,
                R.drawable.ic_writedown,
                getResources().getString(R.string.onboarding_passphrase_write_title),
                getResources().getString(R.string.onboarding_passphrase_write_message),
                getResources().getString(R.string.onboarding_passphrase_write_confirmation),
                null,
                (btnIdx) -> {
                    if(mBinding != null) {
                        HandleBackupOptionCompleted(mBinding.onboardingWriteCheckmark);
                    }
                    return null;
                },
                (dialogContent) -> {

                    // Set the content of the passphrase text field
                    View view = (View)dialogContent;
                    TextView passphraseView = (TextView)view.findViewById(R.id.onboarding_passphrase_content);
                    passphraseView.setText(mPassphrase);

                    // For this dialog, we want to fill the whole screen regardless of the size of the content
                    // 1) Dialog width should be 80% of the width of the screen
                    Display display = getActivity().getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);

                    float idealDialogWidth = size.x * 0.8f;
                    float idealDialogHeight = size.y * 0.8f;

                    view.setLayoutParams(new FrameLayout.LayoutParams((int) idealDialogWidth, (int) idealDialogHeight));

                    return null;
                });


    }


    public void HandleBackupOptionCompleted(View view) {
        if(view != null) {

            Laba.Animate(view, "!s!f!>", () -> { return null; });
            view.setVisibility(View.VISIBLE);

            numberOfBackupOptionsUsed++;

            if (numberOfBackupOptionsUsed >= 2 && mBinding.onboardingDoneButton.isEnabled() == false) {

                Laba.Animate(mBinding.onboardingDoneButton, "^200", () -> { return null; });
                mBinding.onboardingDoneButton.setAlpha(1.0f);
                mBinding.onboardingDoneButton.setEnabled(true);
            }
        }
    }
}

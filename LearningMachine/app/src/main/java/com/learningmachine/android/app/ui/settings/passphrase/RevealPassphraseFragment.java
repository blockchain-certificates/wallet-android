package com.hyland.android.app.ui.settings.passphrase;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyland.android.app.R;
import com.hyland.android.app.data.bitcoin.BitcoinManager;
import com.hyland.android.app.data.inject.Injector;
import com.hyland.android.app.databinding.FragmentRevealPassphraseBinding;
import com.hyland.android.app.ui.LMActivity;
import com.hyland.android.app.ui.LMFragment;
import com.hyland.android.app.util.DialogUtils;
import com.smallplanet.labalib.Laba;

import javax.inject.Inject;

public class RevealPassphraseFragment extends LMFragment {

    @Inject BitcoinManager mBitcoinManager;
    private FragmentRevealPassphraseBinding mBinding;

    public static Fragment newInstance() {
        return new RevealPassphraseFragment();
    }

    private String mPassphrase;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.obtain(getContext())
                .inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_reveal_passphrase,
                container,
                false);

        mBitcoinManager.getPassphrase()
                .compose(bindToMainThread())
                .subscribe(this::configureCurrentPassphraseTextView);


        mBinding.onboardingEmailButton.setOnClickListener(view -> onEmail());
        mBinding.onboardingSaveButton.setOnClickListener(view -> onSave());

        mBinding.onboardingSaveCheckmark.setVisibility(View.INVISIBLE);
        mBinding.onboardingEmailCheckmark.setVisibility(View.INVISIBLE);

        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    private void configureCurrentPassphraseTextView(String currentPassphrase) {
        if (mBinding == null) {
            return;
        }

        mPassphrase = currentPassphrase;

        mBinding.onboardingPassphraseContent.setText(currentPassphrase);
    }



    protected void onSave() {
        ((LMActivity)getActivity()).askToSavePassphraseToDevice(mPassphrase, (passphrase) -> {
            if(passphrase == null) {
                if(Build.VERSION.SDK_INT >= 23) {
                    return;
                }
                DialogUtils.showAlertDialog(getContext(), this,
                        R.drawable.ic_dialog_failure,
                        getResources().getString(R.string.onboarding_passphrase_permissions_error_title),
                        getResources().getString(R.string.onboarding_passphrase_permissions_error),
                        getResources().getString(R.string.ok_button),
                        null,
                        (btnIdx) -> {
                            HandleBackupOptionCompleted(null);
                            return null;
                        });
                return;
            }

            DialogUtils.showAlertDialog(getContext(), this,
                    R.drawable.ic_dialog_success,
                    getResources().getString(R.string.onboarding_passphrase_complete_title),
                    getResources().getString(R.string.onboarding_passphrase_save_complete),
                    getResources().getString(R.string.ok_button),
                    null,
                    (btnIdx) -> {
                        if(mBinding != null) {
                            HandleBackupOptionCompleted(mBinding.onboardingSaveCheckmark);
                        }
                        return null;
                    }, (cancel) -> {
                        if(mBinding != null) {
                            HandleBackupOptionCompleted(mBinding.onboardingSaveCheckmark);
                        }
                        return null;
                    });
        });
    }

    protected void onEmail() {
        DialogUtils.showAlertDialog(getContext(), this,
                0,
                getResources().getString(R.string.onboarding_passphrase_email_before_title),
                getResources().getString(R.string.onboarding_passphrase_email_before),
                getResources().getString(R.string.onboarding_passphrase_cancel),
                getResources().getString(R.string.ok_button),
                (btnIdx) -> {

                    if((int)btnIdx == 0) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Blockcerts Backup");
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

    public void HandleBackupOptionCompleted(View view) {
        if(view != null) {
            Laba.Animate(view, "!s!f!>", () -> { return null; });
            view.setVisibility(View.VISIBLE);
        }
    }

}

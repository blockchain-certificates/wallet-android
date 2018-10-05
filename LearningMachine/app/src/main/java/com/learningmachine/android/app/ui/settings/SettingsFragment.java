package com.learningmachine.android.app.ui.settings;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learningmachine.android.app.BuildConfig;
import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.bitcoin.BitcoinManager;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.databinding.FragmentSettingsBinding;
import com.learningmachine.android.app.ui.LMFragment;
import com.learningmachine.android.app.ui.LMWebActivity;
import com.learningmachine.android.app.ui.cert.AddCertificateActivity;
import com.learningmachine.android.app.ui.issuer.AddIssuerActivity;
import com.learningmachine.android.app.ui.onboarding.OnboardingActivity;
import com.learningmachine.android.app.ui.settings.passphrase.RevealPassphraseActivity;
import com.learningmachine.android.app.util.DialogUtils;
import com.learningmachine.android.app.util.FileLoggingTree;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.inject.Inject;

import timber.log.Timber;


public class SettingsFragment extends LMFragment {

    @Inject
    protected BitcoinManager mBitcoinManager;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.obtain(getContext())
                .inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentSettingsBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_settings,
                container,
                false);

        binding.settingsRevealPassphraseTextView.setOnClickListener(v -> {
            Timber.i("My passphrase tapped in settings");
            Intent intent = RevealPassphraseActivity.newIntent(getContext());
            startActivity(intent);
        });

        setupReplacePassphrase(binding);

        binding.settingsAddIssuerTextView.setOnClickListener(v -> {
            Timber.i("Add Issuer tapped in settings");
            Intent intent = AddIssuerActivity.newIntent(getContext());
            startActivity(intent);
        });

        binding.settingsAddCredentialTextView.setOnClickListener(v -> {
            Timber.i("Add Credential tapped in settings");
            DialogUtils.showCustomSheet(getContext(), this,
                    R.layout.dialog_add_by_file_or_url,
                    0,
                    "",
                    "",
                    "",
                    "",
                    (btnIdx) -> {
                        if ((int)btnIdx == 0) {
                            Timber.i("Add Credential from URL tapped in settings");
                        } else {
                            Timber.i("User has chosen to add a certificate from file");
                        }
                        Intent intent = AddCertificateActivity.newIntent(getContext(), (int)btnIdx, null);
                        startActivity(intent);
                        return null;
                    },
                    (dialogContent) -> {
                        return null;
                    });

        });

        binding.settingsEmailLogsTextView.setOnClickListener(v -> {
            Timber.i("Share device logs");
            String emailData = FileLoggingTree.logAsString();

            //send file using email
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            String to[] = {"techsupport@learningmachine.com"};
            emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
            // the attachment
            emailIntent .putExtra(Intent.EXTRA_TEXT, emailData);
            // the mail subject
            emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Logcat content for Blockcerts");
            emailIntent.setType("message/rfc822");
            startActivity(Intent.createChooser(emailIntent , "Send email..."));
        });

        binding.settingsAboutPassphraseTextView.setOnClickListener(v -> {
            Timber.i("About passphrase tapped in settings");
            String actionBarTitle = getString(R.string.about_passphrases_title);
            String endPoint = getString(R.string.about_passphrases_endpoint);
            Intent intent = LMWebActivity.newIntent(getContext(), actionBarTitle, endPoint);
            startActivity(intent);
        });

        binding.settingsPrivacyPolicyTextView.setOnClickListener(v -> {
            Timber.i("Privacy statement tapped in settings");
            String actionBarTitle = getString(R.string.settings_privacy_policy);
            String endPoint = getString(R.string.settings_privacy_policy_endpoint);
            Intent intent = LMWebActivity.newIntent(getContext(), actionBarTitle, endPoint);
            startActivity(intent);
        });

        return binding.getRoot();
    }

    @Override
    public void onStop() {
        Timber.i("Dismissing the settings screen");
        super.onStop();
    }

    private void setupReplacePassphrase(FragmentSettingsBinding binding) {

        if (BuildConfig.DEBUG == false) {
            binding.settingsLogoutSeparator.setVisibility(View.GONE);
            binding.settingsLogout.setVisibility(View.GONE);
            return;
        }

        binding.settingsLogout.setOnClickListener(v -> {

            DialogUtils.showAlertDialog(getContext(), this,
                    R.drawable.ic_dialog_failure,
                    getResources().getString(R.string.settings_logout_title),
                    getResources().getString(R.string.settings_logout_message),
                    getResources().getString(R.string.settings_logout_button_title),
                    getResources().getString(R.string.onboarding_passphrase_cancel),
                    (btnIdx) -> {
                        if((int)btnIdx == 1) {
                            mBitcoinManager.resetEverything();

                            Intent intent = new Intent(getActivity(), OnboardingActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                        return null;
                    });


        });
    }

}



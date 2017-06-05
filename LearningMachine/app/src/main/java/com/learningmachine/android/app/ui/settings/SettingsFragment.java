package com.learningmachine.android.app.ui.settings;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learningmachine.android.app.BuildConfig;
import com.learningmachine.android.app.R;
import com.learningmachine.android.app.databinding.FragmentSettingsBinding;
import com.learningmachine.android.app.ui.LMFragment;
import com.learningmachine.android.app.ui.LMWebActivity;
import com.learningmachine.android.app.ui.settings.passphrase.ReplacePassphraseActivity;
import com.learningmachine.android.app.ui.settings.passphrase.RevealPassphraseActivity;


public class SettingsFragment extends LMFragment {

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentSettingsBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_settings,
                container,
                false);

        binding.settingsRevealPassphraseTextView.setOnClickListener(v -> {
            Intent intent = RevealPassphraseActivity.newIntent(getContext());
            startActivity(intent);
        });

        setupReplacePassphrase(binding);

        binding.settingsAboutPassphraseTextView.setOnClickListener(v -> {
            String actionBarTitle = getString(R.string.about_passphrases_title);
            String endPoint = getString(R.string.about_passphrases_endpoint);
            Intent intent = LMWebActivity.newIntent(getContext(), actionBarTitle, endPoint);
            startActivity(intent);
        });

        binding.settingsPrivacyPolicyTextView.setOnClickListener(v -> {
            String actionBarTitle = getString(R.string.settings_privacy_policy);
            String endPoint = getString(R.string.settings_privacy_policy_endpoint);
            Intent intent = LMWebActivity.newIntent(getContext(), actionBarTitle, endPoint);
            startActivity(intent);
        });

        return binding.getRoot();
    }

    private void setupReplacePassphrase(FragmentSettingsBinding binding) {
        if (!BuildConfig.DEBUG) {
            binding.settingsReplacePassphraseTextView.setVisibility(View.GONE);
            return;
        }

        binding.settingsReplacePassphraseTextView.setVisibility(View.VISIBLE);
        binding.settingsReplacePassphraseTextView.setOnClickListener(v -> {
            Intent intent = ReplacePassphraseActivity.newIntent(getContext());
            startActivity(intent);
        });
    }
}



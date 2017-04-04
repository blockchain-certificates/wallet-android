package com.learningmachine.android.app.ui.settings;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.controller.LMSupportWebActivity;
import com.learningmachine.android.app.databinding.FragmentSettingsBinding;
import com.learningmachine.android.app.ui.LMFragment;


public class SettingsFragment extends LMFragment {

    private FragmentSettingsBinding mBinding;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);

        mBinding.settingsPrivacyPolicyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String actionBarTitle = getString(R.string.settings_privacy_policy);
                String endPoint = getString(R.string.settings_privacy_policy_endpoint);
                Intent intent = LMSupportWebActivity.newIntent(getContext(), actionBarTitle, endPoint);
                startActivity(intent);
            }
        });

        return mBinding.getRoot();
    }
}



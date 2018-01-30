package com.learningmachine.android.app.ui.onboarding;

import android.app.Fragment;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.preferences.SharedPreferencesManager;
import com.learningmachine.android.app.databinding.FragmentAccountChooserBinding;

import javax.inject.Inject;

public class AccountChooserFragment extends OnboardingFragment {

    private Callback mCallback;
    private FragmentAccountChooserBinding mBinding;

    public interface Callback {
        void onNewAccount();
        void onExistingAccount();
    }

    public static AccountChooserFragment newInstance() {
        return new AccountChooserFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (Callback) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_account_chooser, container, false);

        mBinding.newAccountButton.setOnClickListener(view -> mCallback.onNewAccount());
        mBinding.existingAccountButton.setOnClickListener(view -> mCallback.onExistingAccount());

        mSharedPreferencesManager.setFirstLaunch(true);

        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        OnboardingActivity activity = (OnboardingActivity)getActivity();
        if(activity.isOnAccountsScreen()) {
            checkForDelayedURLsFromDeepLinking();
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
}

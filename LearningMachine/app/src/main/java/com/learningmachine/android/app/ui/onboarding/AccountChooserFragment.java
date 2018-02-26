package com.learningmachine.android.app.ui.onboarding;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.preferences.SharedPreferencesManager;
import com.learningmachine.android.app.databinding.FragmentAccountChooserBinding;
import com.learningmachine.android.app.dialog.AlertDialogFragment;
import com.learningmachine.android.app.util.DialogUtils;
import com.smallplanet.labalib.Laba;

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

        Laba.Animate(mBinding.newAccountButton, "!^300", () -> { return null; });
        Laba.Animate(mBinding.existingAccountButton, "!^300", () -> { return null; });

        mBinding.playVideo.setOnClickListener(view2 -> {

        });

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

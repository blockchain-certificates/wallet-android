package com.learningmachine.android.app.ui.onboarding;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.preferences.SharedPreferencesManager;
import com.learningmachine.android.app.databinding.FragmentAccountChooserBinding;
import com.learningmachine.android.app.databinding.FragmentWelcomeBackBinding;
import com.learningmachine.android.app.ui.video.VideoActivity;
import com.smallplanet.labalib.Laba;

import javax.inject.Inject;

public class WelcomeBackFragment extends OnboardingFragment {

    private FragmentWelcomeBackBinding mBinding;

    public static WelcomeBackFragment newInstance() {
        return new WelcomeBackFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_welcome_back, container, false);


        mBinding.continueButton.setOnClickListener(view -> {
            ((OnboardingActivity)getActivity()).onContinuePastWelcomeScreen();
        });

        mBinding.playVideo.setOnClickListener(view2 -> {
            startActivity(new Intent(getContext(), VideoActivity.class));
        });

        Laba.Animate(mBinding.continueButton, "!^300", () -> { return null; });

        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}

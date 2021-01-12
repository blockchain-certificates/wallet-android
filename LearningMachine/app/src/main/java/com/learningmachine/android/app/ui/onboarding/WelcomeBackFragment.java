package com.learningmachine.android.app.ui.onboarding;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.databinding.FragmentWelcomeBackBinding;
import com.smallplanet.labalib.Laba;

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

package com.learningmachine.android.app.ui.onboarding;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.databinding.ActivityOnboardingBinding;
import com.learningmachine.android.app.ui.LMActivity;
import com.learningmachine.android.app.ui.onboarding.OnboardingFlow.FlowType;

public class OnboardingActivity extends LMActivity implements AccountChooserFragment.Callback,
    GeneratePassphraseFragment.Callback {

    private static final String SAVED_FLOW = "onboardingFlow";

    private OnboardingFlow mOnboardingFlow;
    private OnboardingAdapter mAdapter;
    private ActivityOnboardingBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_onboarding);
        mBinding.onboardingViewpager.addOnPageChangeListener(mOnPageChangeListener);

        if (savedInstanceState == null) {
            mOnboardingFlow = new OnboardingFlow(FlowType.UNKNOWN);
        } else {
            mOnboardingFlow = (OnboardingFlow) savedInstanceState.getSerializable(SAVED_FLOW);
        }

        setupAdapter();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVED_FLOW, mOnboardingFlow);
    }

    @Override
    public void onBackPressed() {
        int position = mOnboardingFlow.getPosition();
        if (position > 0) {
            mOnboardingFlow.setPosition(position - 1);
            mBinding.onboardingViewpager.setCurrentItem(mOnboardingFlow.getPosition());
            return;
        }

        super.onBackPressed();
    }

    @Override
    public void onNewAccount() {
        mOnboardingFlow = new OnboardingFlow(FlowType.NEW_ACCOUNT);
        mOnboardingFlow.setPosition(1);

        mAdapter.setScreens(mOnboardingFlow.getScreens());
        mBinding.onboardingViewpager.getAdapter().notifyDataSetChanged();
        mBinding.onboardingViewpager.setCurrentItem(mOnboardingFlow.getPosition());
    }

    @Override
    public void onExistingAccount() {
        mOnboardingFlow = new OnboardingFlow(FlowType.EXISTING_ACCOUNT);
        mOnboardingFlow.setPosition(1);

        mAdapter.setScreens(mOnboardingFlow.getScreens());
        mBinding.onboardingViewpager.getAdapter().notifyDataSetChanged();
        mBinding.onboardingViewpager.setCurrentItem(mOnboardingFlow.getPosition());
    }

    @Override
    public void onGeneratePassphraseClick() {
        int position = mOnboardingFlow.getPosition() + 1;
        mBinding.onboardingViewpager.setCurrentItem(position);
        mOnboardingFlow.setPosition(position);
    }

    private void setupAdapter() {
        mAdapter = new OnboardingAdapter(getSupportFragmentManager(), mOnboardingFlow.getScreens());
        mBinding.onboardingViewpager.setAdapter(mAdapter);
        mBinding.onboardingViewpager.setCurrentItem(mOnboardingFlow.getPosition());
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) { }

        @Override
        public void onPageSelected(int position) {
            mOnboardingFlow.setPosition(position);
        }

        @Override
        public void onPageScrollStateChanged(int i) { }
    };

}

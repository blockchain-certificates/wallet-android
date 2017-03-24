package com.learningmachine.android.app.ui.home;

import android.support.v4.app.Fragment;

import com.learningmachine.android.app.ui.LearningMachineSingleFragmentActivity;

public class HomeActivity extends LearningMachineSingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return HomeFragment.newInstance();
    }
}

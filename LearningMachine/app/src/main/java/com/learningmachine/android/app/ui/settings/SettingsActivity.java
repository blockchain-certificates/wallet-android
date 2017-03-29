package com.learningmachine.android.app.ui.settings;

import android.support.v4.app.Fragment;

import com.learningmachine.android.app.ui.LearningMachineSingleFragmentActivity;

public class SettingsActivity extends LearningMachineSingleFragmentActivity {


    @Override
    protected Fragment createFragment() {
        return SettingsFragment.newInstance();
    }

}

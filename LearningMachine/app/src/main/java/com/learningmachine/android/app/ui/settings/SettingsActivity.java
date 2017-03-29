package com.learningmachine.android.app.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.learningmachine.android.app.ui.LearningMachineSingleFragmentActivity;

public class SettingsActivity extends LearningMachineSingleFragmentActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    public Fragment createFragment() {
        return SettingsFragment.newInstance();
    }
}

package com.learningmachine.android.app.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.learningmachine.android.app.ui.LMSingleFragmentActivity;

public class SettingsActivity extends LMSingleFragmentActivity {


    public static Intent newIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    public Fragment createFragment() {
        return SettingsFragment.newInstance();
    }

    @Override
    protected boolean requiresBackNavigation() {
        return true;
    }

}

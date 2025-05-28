package com.hyland.android.app.ui.settings;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;

import com.hyland.android.app.ui.LMSingleFragmentActivity;

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

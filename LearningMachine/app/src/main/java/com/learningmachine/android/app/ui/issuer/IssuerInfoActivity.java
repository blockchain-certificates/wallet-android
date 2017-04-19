package com.learningmachine.android.app.ui.issuer;

import android.support.v4.app.Fragment;

import com.learningmachine.android.app.ui.LMSingleFragmentActivity;

public class IssuerInfoActivity extends LMSingleFragmentActivity {


    @Override
    protected Fragment createFragment() {
        return IssuerInfoFragment.newInstance();
    }

    @Override
    protected boolean requiresBackNavigation() {
        return true;
    }
}

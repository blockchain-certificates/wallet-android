package com.learningmachine.android.app.ui.cert;

import android.support.v4.app.Fragment;

import com.learningmachine.android.app.ui.LMSingleFragmentActivity;

public class AddCertificateActivity extends LMSingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return AddCertificateFragment.newInstance();
    }

    @Override
    protected boolean requiresBackNavigation() {
        return true;
    }


}

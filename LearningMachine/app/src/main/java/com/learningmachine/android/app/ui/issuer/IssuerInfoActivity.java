package com.learningmachine.android.app.ui.issuer;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.learningmachine.android.app.data.model.IssuerInfo;
import com.learningmachine.android.app.ui.LMSingleFragmentActivity;

public class IssuerInfoActivity extends LMSingleFragmentActivity {


    public static final String EXTRA_ISSUER_INFO = "IssuerInfoActivity.Info";

    public static Intent newIntent(Context context, IssuerInfo issuerInfo) {
        Intent intent = new Intent(context, IssuerInfoActivity.class);
        intent.putExtra(EXTRA_ISSUER_INFO, issuerInfo);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        IssuerInfo info = (IssuerInfo) getIntent().getSerializableExtra(EXTRA_ISSUER_INFO);
        return IssuerInfoFragment.newInstance(info);
    }

    @Override
    protected boolean requiresBackNavigation() {
        return true;
    }
}

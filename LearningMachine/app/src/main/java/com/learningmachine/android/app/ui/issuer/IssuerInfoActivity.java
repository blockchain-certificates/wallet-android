package com.learningmachine.android.app.ui.issuer;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.learningmachine.android.app.data.model.Issuer;
import com.learningmachine.android.app.ui.LMSingleFragmentActivity;

public class IssuerInfoActivity extends LMSingleFragmentActivity {


    private static final String EXTRA_ISSUER_INFO = "IssuerInfoActivity.Info";

    public static Intent newIntent(Context context, Issuer issuer) {
        Intent intent = new Intent(context, IssuerInfoActivity.class);
        intent.putExtra(EXTRA_ISSUER_INFO, issuer);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        Issuer issuer = (Issuer) getIntent().getSerializableExtra(EXTRA_ISSUER_INFO);
        return IssuerInfoFragment.newInstance(issuer);
    }

    @Override
    protected boolean requiresBackNavigation() {
        return true;
    }
}

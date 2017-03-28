package com.learningmachine.android.app.ui.issuer;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.learningmachine.android.app.data.model.Issuer;
import com.learningmachine.android.app.ui.LearningMachineSingleFragmentActivity;

public class IssuerActivity extends LearningMachineSingleFragmentActivity {

    public static final String EXTRA_ISSUER = "IssuerActivity.Issuer";

    public static Intent newIntent(Context context, Issuer issuer) {
        Intent intent = new Intent(context, IssuerActivity.class);
        intent.putExtra(EXTRA_ISSUER, issuer);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        Issuer issuer = (Issuer) getIntent().getSerializableExtra(EXTRA_ISSUER);
        return IssuerFragment.newInstance(issuer);
    }

    @Override
    public String getActionBarTitle() {
        Issuer issuer = (Issuer) getIntent().getSerializableExtra(EXTRA_ISSUER);
        if (issuer == null) {
            return null;
        }
        return issuer.getName();
    }
}

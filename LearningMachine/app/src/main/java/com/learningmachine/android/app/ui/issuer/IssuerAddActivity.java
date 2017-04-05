package com.learningmachine.android.app.ui.issuer;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.learningmachine.android.app.ui.LMSingleFragmentActivity;

public class IssuerAddActivity extends LMSingleFragmentActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, IssuerAddActivity.class);
    }

    @Override
    protected Fragment createFragment() {
        return IssuerAddFragment.newInstance();
    }
}

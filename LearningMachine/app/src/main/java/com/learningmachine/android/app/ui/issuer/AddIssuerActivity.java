package com.learningmachine.android.app.ui.issuer;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.learningmachine.android.app.ui.LMSingleFragmentActivity;

public class AddIssuerActivity extends LMSingleFragmentActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, AddIssuerActivity.class);
    }

    @Override
    protected Fragment createFragment() {
        return AddIssuerFragment.newInstance();
    }
}

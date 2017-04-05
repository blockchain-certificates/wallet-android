package com.learningmachine.android.app.controller;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class LMSupportWebActivity extends SupportWebActivity {


    private static final String EXTRA_ACTION_BAR_TITLE = "LMSupportWebActivity.ActionBarTitle";
    private static final String EXTRA_END_POINT = "LMSupportWebActivity.EndPoint";

    public static Intent newIntent(Context context, String actionBarTitle, String endPoint) {
        Intent intent = new Intent(context, LMSupportWebActivity.class);
        intent.putExtra(EXTRA_ACTION_BAR_TITLE, actionBarTitle);
        intent.putExtra(EXTRA_END_POINT, endPoint);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        String endPoint = getEndpoint();
        mSupportWebFragment = LMSupportWebFragment.newInstance(endPoint);
        return mSupportWebFragment;
    }

    public String getEndpoint() {
        return getIntent().getStringExtra(EXTRA_END_POINT);
    }

    public String getActionBarTitle() {
        return getIntent().getStringExtra(EXTRA_ACTION_BAR_TITLE);
    }


}

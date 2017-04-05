package com.learningmachine.android.app.controller;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import com.learningmachine.android.app.ui.LMSingleFragmentActivity;

public class LMWebActivity extends LMSingleFragmentActivity {
    protected LMWebFragment mLMWebFragment;

    public LMWebActivity() {
    }

    private static final String EXTRA_ACTION_BAR_TITLE = "LMLMWebActivity.ActionBarTitle";
    private static final String EXTRA_END_POINT = "LMLMWebActivity.EndPoint";

    public static Intent newIntent(Context context, String actionBarTitle, String endPoint) {
        Intent intent = new Intent(context, LMWebActivity.class);
        intent.putExtra(EXTRA_ACTION_BAR_TITLE, actionBarTitle);
        intent.putExtra(EXTRA_END_POINT, endPoint);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        String endPoint = getEndpoint();
        mLMWebFragment = LMWebFragment.newInstance(endPoint);
        return mLMWebFragment;
    }

    public String getEndpoint() {
        return getIntent().getStringExtra(EXTRA_END_POINT);
    }

    public String getActionBarTitle() {
        return getIntent().getStringExtra(EXTRA_ACTION_BAR_TITLE);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == 0) {
            switch (keyCode) {
                case 4:
                    if (this.mLMWebFragment != null) {
                        this.mLMWebFragment.backPressed();
                    }

                    return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

}

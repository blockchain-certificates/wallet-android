package com.learningmachine.android.app.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

public class LMWebActivity extends LMSingleFragmentActivity {
    protected LMWebFragment mLmWebFragment;

    private static final String EXTRA_ACTION_BAR_TITLE = "LMWebActivity.ActionBarTitle";
    private static final String EXTRA_END_POINT = "LMWebActivity.EndPoint";

    public static Intent newIntent(Context context, String actionBarTitle, String endPoint) {
        Intent intent = new Intent(context, LMWebActivity.class);
        intent.putExtra(EXTRA_ACTION_BAR_TITLE, actionBarTitle);
        intent.putExtra(EXTRA_END_POINT, endPoint);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        String endPoint = getEndpoint();
        mLmWebFragment = LMWebFragment.newInstance(endPoint);
        return mLmWebFragment;
    }

    public String getEndpoint() {
        return getIntent().getStringExtra(EXTRA_END_POINT);
    }

    public String getActionBarTitle() {
        return getIntent().getStringExtra(EXTRA_ACTION_BAR_TITLE);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (mLmWebFragment != null) {
                        mLmWebFragment.backPressed();
                    }

                    return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected boolean requiresBackNavigation() {
        return true;
    }
}

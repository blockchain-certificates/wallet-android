package com.learningmachine.android.app.controller;

import android.view.KeyEvent;

import com.learningmachine.android.app.ui.LMSingleFragmentActivity;

public abstract class SupportWebActivity extends LMSingleFragmentActivity {
    protected SupportWebFragment mSupportWebFragment;

    public SupportWebActivity() {
    }

    public abstract String getEndpoint();

    public abstract String getActionBarTitle();

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == 0) {
            switch (keyCode) {
                case 4:
                    if (this.mSupportWebFragment != null) {
                        this.mSupportWebFragment.backPressed();
                    }

                    return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }
}

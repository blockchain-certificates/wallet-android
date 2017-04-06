package com.learningmachine.android.app.ui.settings.passphrase;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.learningmachine.android.app.ui.LMSingleFragmentActivity;

public class RevealPassphraseActivity extends LMSingleFragmentActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, RevealPassphraseActivity.class);
    }

    @Override
    protected Fragment createFragment() {
        return RevealPassphraseFragment.newInstance();
    }

    @Override
    protected boolean requiresBackNavigation() {
        return true;
    }
}

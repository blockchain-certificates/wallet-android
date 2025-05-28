package com.hyland.android.app.ui.settings.passphrase;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;

import com.hyland.android.app.ui.LMSingleFragmentActivity;

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

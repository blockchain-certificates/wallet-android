package com.learningmachine.android.app.ui.issuer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.learningmachine.android.app.ui.LMSingleFragmentActivity;

public class AddIssuerActivity extends LMSingleFragmentActivity {

    private static final String EXTRA_ISSUER_URL = "AddIssuerActivity.IssuerUrl";
    private static final String EXTRA_ISSUER_NONCE = "AddIssuerActivity.IssuerNonce";

    private AddIssuerFragment lastFragment;

    public static Intent newIntent(Context context) {
        return newIntent(context, null, null);
    }

    public static Intent newIntent(Context context, String issuerUrlString, String nonce) {
        Intent intent = new Intent(context, AddIssuerActivity.class);
        intent.putExtra(EXTRA_ISSUER_URL, issuerUrlString);
        intent.putExtra(EXTRA_ISSUER_NONCE, nonce);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        String issuerUrlString = getIntent().getStringExtra(EXTRA_ISSUER_URL);
        String nonce = getIntent().getStringExtra(EXTRA_ISSUER_NONCE);
        lastFragment = AddIssuerFragment.newInstance(issuerUrlString, nonce);
        return lastFragment;
    }

    @Override
    protected boolean requiresBackNavigation() {
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        lastFragment.updateArgs(
                intent.getStringExtra(EXTRA_ISSUER_URL),
                intent.getStringExtra(EXTRA_ISSUER_NONCE));

    }
}

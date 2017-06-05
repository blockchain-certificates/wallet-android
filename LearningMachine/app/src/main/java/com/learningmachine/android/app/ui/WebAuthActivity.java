package com.learningmachine.android.app.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

public class WebAuthActivity extends LMSingleFragmentActivity implements WebAuthFragment.WebAuthCallbacks {
    private static final String WEB_AUTH_SUCCESS = "WebAuthActivity.WebAuthSuccess";
    protected WebAuthFragment mWebAuthFragment;

    private static final String EXTRA_END_POINT = "WebAuthActivity.EndPoint";
    private static final String EXTRA_NONCE = "WebAuthActivity.Nonce";
    private static final String EXTRA_BITCOIN_ADDRESS = "WebAuthActivity.BitcoinAddress";
    private static final String EXTRA_SUCCESS_URL = "WebAuthActivity.SuccessURL";
    private static final String EXTRA_ERROR_URL = "WebAuthActivity.ErrorURL";

    public static Intent newIntent(Context context, String endPoint, String nonce, String bitcoinAddress, String successURL, String errorURL) {
        Intent intent = new Intent(context, WebAuthActivity.class);
        intent.putExtra(EXTRA_END_POINT, endPoint);
        intent.putExtra(EXTRA_NONCE, nonce);
        intent.putExtra(EXTRA_BITCOIN_ADDRESS, bitcoinAddress);
        intent.putExtra(EXTRA_SUCCESS_URL, successURL);
        intent.putExtra(EXTRA_ERROR_URL, errorURL);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        String endPoint = getEndpoint();
        String nonce = getNonce();
        String bitcoinAddress = getBitcoinAddress();
        String successURL = getSuccessURL();
        String errorURL = getErrorURL();
        mWebAuthFragment = WebAuthFragment.newInstance(endPoint, nonce, bitcoinAddress, successURL, errorURL);
        return mWebAuthFragment;
    }

    private String getEndpoint() {
        return getIntent().getStringExtra(EXTRA_END_POINT);
    }

    private String getNonce() {
        return getIntent().getStringExtra(EXTRA_NONCE);
    }

    private String getBitcoinAddress() {
        return getIntent().getStringExtra(EXTRA_BITCOIN_ADDRESS);
    }

    private String getSuccessURL() {
        return getIntent().getStringExtra(EXTRA_SUCCESS_URL);
    }

    private String getErrorURL() {
        return getIntent().getStringExtra(EXTRA_ERROR_URL);
    }

    public String getActionBarTitle() {
        return "Web Auth";
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (mWebAuthFragment != null) {
                        mWebAuthFragment.backPressed();
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

    @Override
    public void onSuccess() {
        Intent result = new Intent();
        result.putExtra(WEB_AUTH_SUCCESS, true);
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    public void onError() {
        Intent result = new Intent();
        result.putExtra(WEB_AUTH_SUCCESS, false);
        setResult(RESULT_OK);
        finish();
    }

    public static boolean isWebAuthSuccess(Intent result) {
        return result != null && result.getBooleanExtra(WEB_AUTH_SUCCESS, false);
    }
}

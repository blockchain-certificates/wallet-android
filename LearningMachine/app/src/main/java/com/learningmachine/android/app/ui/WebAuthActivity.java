package com.learningmachine.android.app.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.webservice.request.IssuerIntroductionRequest;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import timber.log.Timber;

public class WebAuthActivity extends LMSingleFragmentActivity implements WebAuthFragment.WebAuthCallbacks {
    private static final String WEB_AUTH_SUCCESS = "WebAuthActivity.WebAuthSuccess";

    protected WebAuthFragment mWebAuthFragment;

    private static final String EXTRA_END_POINT = "WebAuthActivity.EndPoint";
    private static final String EXTRA_SUCCESS_URL = "WebAuthActivity.SuccessURL";
    private static final String EXTRA_ERROR_URL = "WebAuthActivity.ErrorURL";
    private static final String EXTRA_BITCOIN_ADDRESS = "WebAuthActivity.BitcoinAddress";

    public static Intent newIntent(Context context, IssuerIntroductionRequest request) {
        IssuerResponse issuer = request.getIssuerResponse();
        String endPoint = issuer.getIntroUrl();
        String successUrl = issuer.getIntroductionSuccessUrlString();
        String errorUrl = issuer.getIntroductionErrorUrlString();
        String bitcoinAddress = request.getBitcoinAddress();
        String nonce = request.getNonce();

        Intent intent = new Intent(context, WebAuthActivity.class);
        intent.putExtra(EXTRA_SUCCESS_URL, successUrl);
        intent.putExtra(EXTRA_ERROR_URL, errorUrl);
        try {
            String url = endPoint + "?bitcoinAddress=" + URLEncoder.encode(bitcoinAddress, "UTF-8") + "&nonce=" + URLEncoder.encode(nonce, "UTF-8");
            intent.putExtra(EXTRA_END_POINT, url);
        } catch (UnsupportedEncodingException e) {
            Timber.e(e, "Could not create the web auth request for issuer introduction");
        }

        return intent;
    }

    @Override
    protected Fragment createFragment() {
        String endPoint = getEndpoint();
        String successUrlString = getSuccessUrlString();
        String errorUrlString = getErrorUrlString();
        mWebAuthFragment = WebAuthFragment.newInstance(endPoint, successUrlString, errorUrlString);
        return mWebAuthFragment;
    }

    private String getEndpoint() {
        return getIntent().getStringExtra(EXTRA_END_POINT);
    }

    private String getSuccessUrlString() {
        return getIntent().getStringExtra(EXTRA_SUCCESS_URL);
    }

    private String getErrorUrlString() {
        return getIntent().getStringExtra(EXTRA_ERROR_URL);
    }

    private String getBitcoinAddress() {
        return getIntent().getStringExtra(EXTRA_BITCOIN_ADDRESS);
    }

    public String getActionBarTitle() {
        return getString(R.string.login_to_issuer);
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
        result.putExtra(EXTRA_BITCOIN_ADDRESS, getBitcoinAddress());
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    public void onError() {
        Intent result = new Intent();
        result.putExtra(WEB_AUTH_SUCCESS, false);
        result.putExtra(EXTRA_BITCOIN_ADDRESS, getBitcoinAddress());
        setResult(RESULT_OK);
        finish();
    }

    public static boolean isWebAuthSuccess(Intent result) {
        return result != null && result.getBooleanExtra(WEB_AUTH_SUCCESS, false);
    }

    public static String getWebAuthBitcoinAddress(Intent result) {
        return result != null ? result.getStringExtra(EXTRA_BITCOIN_ADDRESS) : null;
    }
}

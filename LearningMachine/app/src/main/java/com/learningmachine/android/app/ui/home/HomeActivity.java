package com.learningmachine.android.app.ui.home;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.ui.LMSingleFragmentActivity;

public class HomeActivity extends LMSingleFragmentActivity {

    private static final String EXTRA_ISSUER_URL = "HomeActivity.IssuerUrl";
    private static final String EXTRA_CERT_URL = "HomeActivity.CertUrl";
    private static final String EXTRA_ISSUER_NONCE = "HomeActivity.IssuerNonce";
    private static final String EXTRA_LINK_TYPE = "HomeActivity.LinkType";

    public static final String LINK_TYPE_ISSUER = "HomeActivity.LinkTypeIssuer";
    public static final String LINK_TYPE_CERT = "HomeActivity.LinkTypeCert";

    private HomeFragment mLastFragment;

    public static Intent newIntentForIssuer(Context context, String issuerUrlString, String nonce) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(EXTRA_ISSUER_URL, issuerUrlString);
        intent.putExtra(EXTRA_ISSUER_NONCE, nonce);
        intent.putExtra(EXTRA_LINK_TYPE, LINK_TYPE_ISSUER);
        return intent;
    }

    public static Intent newIntentForCert(Context context, String certUrl) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(EXTRA_CERT_URL, certUrl);
        intent.putExtra(EXTRA_LINK_TYPE, LINK_TYPE_CERT);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        String linkType = getIntent().getStringExtra(EXTRA_LINK_TYPE);
        if (LINK_TYPE_ISSUER.equals(linkType)) {
            String issuerUrlString = getIntent().getStringExtra(EXTRA_ISSUER_URL);
            String nonce = getIntent().getStringExtra(EXTRA_ISSUER_NONCE);
            mLastFragment = HomeFragment.newInstanceForIssuer(issuerUrlString, nonce);
        } else if (LINK_TYPE_CERT.equals(linkType)) {
            String certUrl = getIntent().getStringExtra(EXTRA_CERT_URL);
            mLastFragment = HomeFragment.newInstanceForCert(certUrl);
        } else {
            mLastFragment = HomeFragment.newInstance();
        }
        return mLastFragment;
    }

    private HomeFragment getLastFragment() {
        if (mLastFragment == null) {
            //Get from super
            mLastFragment = (HomeFragment) getFragment();
        }
        return mLastFragment;
    }

    @Override
    public String getActionBarTitle() {
        return getString(R.string.home_issuers);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String linkType = intent.getStringExtra(EXTRA_LINK_TYPE);
        if (LINK_TYPE_ISSUER.equals(linkType)) {
            String issuerUrl = intent.getStringExtra(EXTRA_ISSUER_URL);
            String nounce = intent.getStringExtra(EXTRA_ISSUER_NONCE);
            getLastFragment().updateArgsIssuer(issuerUrl, nounce);
        } else if (LINK_TYPE_CERT.equals(linkType)) {
            String certUrl = intent.getStringExtra(EXTRA_CERT_URL);
            getLastFragment().updateArgsCert(certUrl);
        }

    }
}

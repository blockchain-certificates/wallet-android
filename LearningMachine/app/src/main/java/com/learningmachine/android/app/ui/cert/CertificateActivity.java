package com.learningmachine.android.app.ui.cert;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.model.Certificate;
import com.learningmachine.android.app.ui.LMSingleFragmentActivity;

public class CertificateActivity extends LMSingleFragmentActivity {

    public static final String EXTRA_CERTIFICATE = "CertificateActivity.Certificate";

    public static Intent newIntent(Context context, Certificate certificate) {
        Intent intent = new Intent(context, CertificateActivity.class);
        intent.putExtra(EXTRA_CERTIFICATE, certificate);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        Certificate certificate = (Certificate) getIntent().getSerializableExtra(EXTRA_CERTIFICATE);
        return CertificateFragment.newInstance(certificate);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        }
    }

    @Override
    public String getActionBarTitle() {
        return getString(R.string.fragment_certificate_title);
    }

    @Override
    protected boolean requiresBackNavigation() {
        return true;
    }
}

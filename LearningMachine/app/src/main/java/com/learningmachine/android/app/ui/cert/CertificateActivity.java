package com.learningmachine.android.app.ui.cert;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

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
    public String getActionBarTitle() {
        Certificate certificate = (Certificate) getIntent().getSerializableExtra(EXTRA_CERTIFICATE);
        if (certificate == null) {
            return null;
        }
        return certificate.getName();
    }

    @Override
    protected boolean requiresBackNavigation() {
        return true;
    }

}

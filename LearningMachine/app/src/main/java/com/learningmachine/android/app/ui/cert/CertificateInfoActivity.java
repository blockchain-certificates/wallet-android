package com.learningmachine.android.app.ui.cert;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.learningmachine.android.app.data.model.Certificate;
import com.learningmachine.android.app.ui.LMSingleFragmentActivity;

public class CertificateInfoActivity extends LMSingleFragmentActivity {

    private static final String EXTRA_CERTIFICATE = "CertificateInfoActivity.Certificate";

    public static Intent newIntent(Context context, Certificate certificate) {
        Intent intent = new Intent(context, CertificateInfoActivity.class);
        intent.putExtra(EXTRA_CERTIFICATE, certificate);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        Certificate certificate = (Certificate) getIntent().getSerializableExtra(EXTRA_CERTIFICATE);
        return CertificateInfoFragment.newInstance(certificate);
    }

    @Override
    protected boolean requiresBackNavigation() {
        return true;
    }
}

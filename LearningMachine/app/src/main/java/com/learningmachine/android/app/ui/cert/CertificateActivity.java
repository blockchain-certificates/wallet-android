package com.learningmachine.android.app.ui.cert;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.ui.LMSingleFragmentActivity;

public class CertificateActivity extends LMSingleFragmentActivity {

    public static final String EXTRA_CERTIFICATE_UUID = "CertificateActivity.CertificateUuid";

    public static Intent newIntent(Context context, String certificateUuid) {
        Intent intent = new Intent(context, CertificateActivity.class);
        intent.putExtra(EXTRA_CERTIFICATE_UUID, certificateUuid);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        String certificateUuid = getIntent().getStringExtra(EXTRA_CERTIFICATE_UUID);
        return CertificateFragment.newInstance(certificateUuid);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

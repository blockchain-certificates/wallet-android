package com.hyland.android.app.ui.cert;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;

import com.hyland.android.app.R;
import com.hyland.android.app.ui.LMSingleFragmentActivity;

public class VerifyCertificateActivity  extends LMSingleFragmentActivity {

    public static final String EXTRA_CERTIFICATE_UUID = "VerifyCertificateActivity.CertificateUuid";

    public static Intent newIntent(Context context, String certificateUuid) {
        Intent intent = new Intent(context, VerifyCertificateActivity.class);
        intent.putExtra(EXTRA_CERTIFICATE_UUID, certificateUuid);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        String certificateUuid = getIntent().getStringExtra(EXTRA_CERTIFICATE_UUID);
        return VerifyCertificateFragment.newInstance(certificateUuid);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_close);
        }
    }

    @Override
    public String getActionBarTitle() {
        return getString(R.string.verification_header);
    }

    @Override
    protected boolean requiresBackNavigation() {
        return true;
    }

}

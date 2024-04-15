package com.learningmachine.android.app.ui.cert;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.ui.LMSingleFragmentActivity;

public class SelectiveDisclosureCertificateActivity extends LMSingleFragmentActivity {

    public static final String EXTRA_CERTIFICATE_UUID = "SelectiveDisclosureCertificateActivity.CertificateUuid";

    public static Intent newIntent(Context context, String certificateUuid) {
        Intent intent = new Intent(context, SelectiveDisclosureCertificateActivity.class);
        intent.putExtra(EXTRA_CERTIFICATE_UUID, certificateUuid);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        String certificateUuid = getIntent().getStringExtra(EXTRA_CERTIFICATE_UUID);
        return SelectiveDisclosureCertificateFragment.newInstance(certificateUuid);
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
        return getString(R.string.selective_disclosure_header);
    }

    @Override
    protected boolean requiresBackNavigation() {
        return true;
    }

}

package com.hyland.android.app.ui.issuer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import com.hyland.android.app.R;
import com.hyland.android.app.data.IssuerManager;
import com.hyland.android.app.data.inject.Injector;
import com.hyland.android.app.ui.LMSingleFragmentActivity;

import javax.inject.Inject;

public class IssuerActivity extends LMSingleFragmentActivity {

    public static final String EXTRA_ISSUER_UUID = "IssuerActivity.IssuerUuid";

    @Inject protected IssuerManager mIssuerManager;

    private String mIssuerName;

    public static Intent newIntent(Context context, String issuerUuid) {
        Intent intent = new Intent(context, IssuerActivity.class);
        intent.putExtra(EXTRA_ISSUER_UUID, issuerUuid);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        String issuerUuid = getIntent().getStringExtra(EXTRA_ISSUER_UUID);
        return IssuerFragment.newInstance(issuerUuid);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.obtain(this)
                .inject(this);

        String issuerUuid = getIntent().getStringExtra(EXTRA_ISSUER_UUID);
        mIssuerManager.getIssuer(issuerUuid)
                .compose(bindToMainThread())
                .subscribe(issuer -> {
                    mIssuerName = issuer.getName();
                    setupActionBar();
                });
    }

    @Override
    public String getActionBarTitle() {
        return getString(R.string.fragment_issuer_title);
    }

    @Override
    protected boolean requiresBackNavigation() {
        return true;
    }
}

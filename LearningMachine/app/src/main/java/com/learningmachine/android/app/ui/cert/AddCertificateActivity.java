package com.learningmachine.android.app.ui.cert;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.databinding.ActivityAddCertificateBinding;
import com.learningmachine.android.app.ui.LMActivity;

import java.util.ArrayList;
import java.util.List;


public class AddCertificateActivity extends LMActivity {

    private static final String EXTRA_CERT_URL = "AddIssuerActivity.CertUrl";
    private static final String PAGER_INDEX = "AddIssuerActivity.PagerIndex";

    private ActivityAddCertificateBinding mBinding;
    private AddCertificateURLFragment lastURLFragment = null;

    public static Intent newIntent(Context context, int pagerIndex, String certificateUrlString) {
        Intent intent = new Intent(context, AddCertificateActivity.class);
        intent.putExtra(EXTRA_CERT_URL, certificateUrlString);
        intent.putExtra(PAGER_INDEX, pagerIndex);
        return intent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_certificate);
        setSupportActionBar(mBinding.addCertificateToolbar);

        setupViewPager(mBinding.activityCertificatePagerViewPager);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(lastURLFragment != null) {
            lastURLFragment.updateArgs(intent.getStringExtra(EXTRA_CERT_URL));
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        List<CertificateType> certificateTypes = new ArrayList<>();
        certificateTypes.add(CertificateType.URL);
        certificateTypes.add(CertificateType.FILE);

        AddCertificateViewPagerAdapter adapter = new AddCertificateViewPagerAdapter(this,
                getSupportFragmentManager(),
                certificateTypes);
        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(getIntent().getIntExtra(PAGER_INDEX, 0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fragment_add_certificate, menu);
        return true;
    }

    @Override
    protected boolean requiresBackNavigation() {
        return true;
    }

    private enum CertificateType {
        URL(R.string.add_certificate_by_url),
        FILE(R.string.add_certificate_by_file);

        private int mTitleResId;

        CertificateType(int titleResId) {
            mTitleResId = titleResId;
        }

        public int getTitleResId() {
            return mTitleResId;
        }
    }

    private class AddCertificateViewPagerAdapter extends FragmentStatePagerAdapter {
        private List<CertificateType> mCertificateTypes;
        private Context mContext;

        public AddCertificateViewPagerAdapter(Context context, FragmentManager manager, List<CertificateType> certificateTypes) {
            super(manager);
            mContext = context;
            mCertificateTypes = certificateTypes;
        }

        @Override
        public Fragment getItem(int position) {
            CertificateType certificateType = mCertificateTypes.get(position);
            switch (certificateType) {
                case URL:
                    String certUrlString = getIntent().getStringExtra(EXTRA_CERT_URL);
                    lastURLFragment = AddCertificateURLFragment.newInstance(certUrlString);
                    return lastURLFragment;
                case FILE:
                    return AddCertificateFileFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return mCertificateTypes.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {

            CertificateType certificateType = mCertificateTypes.get(position);
            return mContext.getString(certificateType.getTitleResId());
        }
    }
}

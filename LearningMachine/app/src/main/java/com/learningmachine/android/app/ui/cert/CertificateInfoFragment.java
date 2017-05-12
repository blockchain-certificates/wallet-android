package com.learningmachine.android.app.ui.cert;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.CertificateManager;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.data.model.Certificate;
import com.learningmachine.android.app.databinding.FragmentCertificateInfoBinding;
import com.learningmachine.android.app.dialog.AlertDialogFragment;
import com.learningmachine.android.app.ui.LMFragment;
import com.learningmachine.android.app.ui.issuer.IssuerActivity;
import com.learningmachine.android.app.util.DialogUtils;

import javax.inject.Inject;

import timber.log.Timber;

public class CertificateInfoFragment extends LMFragment {

    private static final String ARG_CERTIFICATE_UUID = "CertificateInfoFragment.CertificateUuid";
    private static final int REQUEST_CODE = 999;

    @Inject CertificateManager mCertificateManager;

    private FragmentCertificateInfoBinding mBinding;
    private Certificate mCertificate;

    public static CertificateInfoFragment newInstance(String certificateUuid) {
        Bundle args = new Bundle();
        args.putString(ARG_CERTIFICATE_UUID, certificateUuid);

        CertificateInfoFragment fragment = new CertificateInfoFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.obtain(getContext())
                .inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_certificate_info, container, false);

        String certificateUuid = getArguments().getString(ARG_CERTIFICATE_UUID);
        mCertificateManager.getCertificate(certificateUuid)
                .compose(bindToMainThread())
                .subscribe(certificate -> {
                    mCertificate = certificate;
                    CertificateInfoViewModel viewModel = new CertificateInfoViewModel(certificate);
                    mBinding.setCertificateInfo(viewModel);
                }, throwable -> Timber.e(throwable, "Unable to load certificate"));

        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_certificate_info, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fragment_certificate_info_delete_menu_item:
                displayAlert(REQUEST_CODE,
                        R.string.fragment_certificate_info_delete_warning_title,
                        R.string.fragment_certificate_info_delete_warning_message,
                        R.string.fragment_certificate_info_delete_warning_positive_title,
                        R.string.fragment_certificate_info_delete_warning_negative_title);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == AlertDialogFragment.RESULT_POSITIVE) {
            String uuid = mCertificate.getUuid();
            mCertificateManager.removeCertificate(uuid)
                    .compose(bindToMainThread())
                    .subscribe(success -> {
                        String issuerUuid = mCertificate.getIssuerUuid();
                        Intent intent = IssuerActivity.newIntent(getContext(), issuerUuid);
                        startActivity(intent);
                    });
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

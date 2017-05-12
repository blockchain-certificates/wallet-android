package com.learningmachine.android.app.ui.cert;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.CertificateManager;
import com.learningmachine.android.app.data.IssuerManager;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.data.model.Certificate;
import com.learningmachine.android.app.databinding.FragmentCertificateInfoBinding;
import com.learningmachine.android.app.ui.LMFragment;

import javax.inject.Inject;

import timber.log.Timber;

public class CertificateInfoFragment extends LMFragment {

    private static final String ARG_CERTIFICATE_UUID = "CertificateInfoFragment.CertificateUuid";

    @Inject protected CertificateManager mCertificateManager;
    @Inject protected IssuerManager mIssuerManager;
    private Certificate mCertificate;

    private FragmentCertificateInfoBinding mInfoBinding;

    public static CertificateInfoFragment newInstance(String uuid) {
        Bundle args = new Bundle();
        args.putString(ARG_CERTIFICATE_UUID, uuid);

        CertificateInfoFragment fragment = new CertificateInfoFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.obtain(getContext())
                .inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mInfoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_certificate_info, container, false);

        String certificateUuid = getArguments().getString(ARG_CERTIFICATE_UUID);
        mCertificateManager.getCertificate(certificateUuid)
                .flatMap(certificate -> {
                    mCertificate = certificate;
                    String issuerUuid = certificate.getIssuerUuid();
                    return mIssuerManager.getIssuer(issuerUuid);
                })
                .compose(bindToMainThread())
                .subscribe(issuer -> {
                    CertificateInfoViewModel viewModel = new CertificateInfoViewModel(mCertificate, issuer);
                    mInfoBinding.setCertificateInfo(viewModel);

                },throwable -> Timber.e(throwable, "Unable to load certificate & issuer"));

        return mInfoBinding.getRoot();
    }
}

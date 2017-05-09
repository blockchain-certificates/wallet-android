package com.learningmachine.android.app.ui.cert;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.model.Certificate;
import com.learningmachine.android.app.databinding.FragmentCertificateInfoBinding;
import com.learningmachine.android.app.ui.LMFragment;

public class CertificateInfoFragment extends LMFragment {

    private static final String ARG_CERTIFICATE = "CertificateInfoFragment.Certificate";

    private FragmentCertificateInfoBinding mInfoBinding;
    private Certificate mCertificate;

    public static CertificateInfoFragment newInstance(Certificate certificate) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CERTIFICATE, certificate);

        CertificateInfoFragment fragment = new CertificateInfoFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCertificate = (Certificate) getArguments().getSerializable(ARG_CERTIFICATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mInfoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_certificate_info, container, false);

        CertificateInfoViewModel viewModel = new CertificateInfoViewModel(mCertificate);
        mInfoBinding.setCertificateInfo(viewModel);
        return mInfoBinding.getRoot();
    }
}

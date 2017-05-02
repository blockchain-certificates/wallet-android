package com.learningmachine.android.app.ui.cert;

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
import com.learningmachine.android.app.data.model.Certificate;
import com.learningmachine.android.app.databinding.FragmentCertificateBinding;
import com.learningmachine.android.app.ui.LMFragment;

public class CertificateFragment extends LMFragment {

    private static final String ARG_CERTIFICATE = "CertificateFragment.Certificate";

    private Certificate mCertificate;
    private FragmentCertificateBinding mBinding;

    public static CertificateFragment newInstance(Certificate certificate) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CERTIFICATE, certificate);

        CertificateFragment fragment = new CertificateFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mCertificate = (Certificate) getArguments().getSerializable(ARG_CERTIFICATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_certificate, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_certificate, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fragment_certificate_verify_menu_item:
                return true;
            case R.id.fragment_certificate_share_menu_item:
                return true;
            case R.id.fragment_certificate_info_menu_item:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

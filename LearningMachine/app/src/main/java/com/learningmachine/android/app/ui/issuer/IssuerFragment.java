package com.learningmachine.android.app.ui.issuer;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.model.Certificate;
import com.learningmachine.android.app.data.model.Issuer;
import com.learningmachine.android.app.data.model.IssuerInfo;
import com.learningmachine.android.app.databinding.FragmentIssuerBinding;
import com.learningmachine.android.app.databinding.ListItemCertificateBinding;
import com.learningmachine.android.app.ui.LMFragment;

import java.util.ArrayList;
import java.util.List;

public class IssuerFragment extends LMFragment {

    private static final String ARG_ISSUER = "IssuerFragment.Issuer";

    private Issuer mIssuer;
    private IssuerInfo mIssuerInfo;
    private FragmentIssuerBinding mBinding;

    public static IssuerFragment newInstance(Issuer issuer) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_ISSUER, issuer);

        IssuerFragment fragment = new IssuerFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mIssuer = (Issuer) getArguments().getSerializable(ARG_ISSUER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_issuer, container, false);

        setupRecyclerView();

        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_issuer, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.certificate_info_icon:
                mIssuerInfo = new IssuerInfo("April, 4th, 2017", "rekbrgregbr", "google.com", "rashad@bignerdranch.com", "sample");
                Intent intent = IssuerInfoActivity.newIntent(getContext(), mIssuerInfo);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView() {
        // build list
        List<Certificate> certificateList = new ArrayList<>();

        Certificate certificate = new Certificate("Sample Certificate 1", "Welcome to the sample certificate!");
        certificateList.add(certificate);
        certificate = new Certificate("Sample Certificate 2", "Welcome to the sample certificate, again!");
        certificateList.add(certificate);
        certificate = new Certificate("Sample Certificate 3", "Okay, we get it by now. It’s a certificate.");
        certificateList.add(certificate);

        CertificateAdapter adapter = new CertificateAdapter(certificateList);
        mBinding.certificateRecyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mBinding.certificateRecyclerView.setLayoutManager(layoutManager);
    }

    private class CertificateAdapter extends RecyclerView.Adapter<CertificateViewHolder> {

        private List<Certificate> mCertificateList;

        CertificateAdapter(List<Certificate> certificateList) {
            mCertificateList = certificateList;
        }

        @Override
        public CertificateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            ListItemCertificateBinding binding = DataBindingUtil.inflate(inflater,
                    R.layout.list_item_certificate,
                    parent,
                    false);
            return new CertificateViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(CertificateViewHolder holder, int position) {
            Certificate certificate = mCertificateList.get(position);
            holder.bind(certificate);
        }

        @Override
        public int getItemCount() {
            return mCertificateList.size();
        }
    }
}

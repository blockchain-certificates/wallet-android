package com.learningmachine.android.app.ui.issuer;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.CertificateManager;
import com.learningmachine.android.app.data.IssuerManager;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.data.model.CertificateRecord;
import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.data.model.IssuingEstimate;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;
import com.learningmachine.android.app.databinding.FragmentIssuerBinding;
import com.learningmachine.android.app.databinding.ListItemCertificateBinding;
import com.learningmachine.android.app.ui.LMFragment;
import com.learningmachine.android.app.ui.cert.AddCertificateActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import timber.log.Timber;

public class IssuerFragment extends LMFragment {

    private static final String ARG_ISSUER_UUID = "IssuerFragment.IssuerUuid";

    @Inject protected CertificateManager mCertificateManager;
    @Inject protected IssuerManager mIssuerManager;

    private String mIssuerUuid;
    private FragmentIssuerBinding mBinding;

    private List<CertificateRecord> mCertificateList;
    private List<IssuingEstimate> mEstimateList;

    public static IssuerFragment newInstance(String issuerUuid) {
        Bundle args = new Bundle();
        args.putString(ARG_ISSUER_UUID, issuerUuid);

        IssuerFragment fragment = new IssuerFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.obtain(getContext())
                .inject(this);

        mIssuerUuid = getArguments().getString(ARG_ISSUER_UUID);
        mCertificateList = new ArrayList<>();
        mEstimateList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_issuer, container, false);

        mBinding.addCertificateFloatingActionButton.setOnClickListener(v -> {
            Intent intent = AddCertificateActivity.newIntent(getContext());
            startActivity(intent);
        });

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
            case R.id.fragment_issuer_info_menu_item:
                Intent intent = IssuerInfoActivity.newIntent(getContext(), mIssuerUuid);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        mCertificateManager.getCertificatesForIssuer(mIssuerUuid)
                .compose(bindToMainThread())
                .subscribe(this::updateRecyclerView, throwable -> Timber.e(throwable, "Unable to load certificates"));

        Observable.zip(mIssuerManager.getIssuer(mIssuerUuid),
                mIssuerManager.fetchIssuer(mIssuerUuid),
                (record, response) -> {
                    response.setRecipientPubKey(record.getRecipientPubKey());
                    return response;
                })
                .subscribe(this::fetchIssuingEstimates);
    }

    private void fetchIssuingEstimates(IssuerResponse issuerResponse) {
        mIssuerManager.getIssuingEstimates(issuerResponse.getIssuingEstimateUrlString(), issuerResponse.getRecipientPubKey())
                .compose(bindToMainThread())
                .subscribe(this::updateRecyclerViewWithEstimates);
    }

    private void setupRecyclerView() {
        CertificateAdapter adapter = new CertificateAdapter(mCertificateList, mEstimateList);
        mBinding.certificateRecyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mBinding.certificateRecyclerView.setLayoutManager(layoutManager);
    }

    private void updateRecyclerView(List<CertificateRecord> certificateList) {
        mCertificateList.clear();
        mCertificateList.addAll(certificateList);
        mBinding.certificateRecyclerView.getAdapter()
                .notifyDataSetChanged();
    }

    private void updateRecyclerViewWithEstimates(List<IssuingEstimate> estimateList) {
        mEstimateList.clear();
        mEstimateList.addAll(estimateList);
        mBinding.certificateRecyclerView.getAdapter().notifyDataSetChanged();
    }

    private class CertificateAdapter extends RecyclerView.Adapter<CertificateViewHolder> {
        private List<CertificateRecord> mCertificateList;
        private List<IssuingEstimate> mEstimateList;

        CertificateAdapter(List<CertificateRecord> certificateList, List<IssuingEstimate> estimateList) {
            mCertificateList = certificateList;
            mEstimateList = estimateList;
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
            if (position < mEstimateList.size()) {
                IssuingEstimate estimate = mEstimateList.get(position);
                holder.bind(estimate);
            } else {
                position -= mEstimateList.size();
                CertificateRecord certificate = mCertificateList.get(position);
                holder.bind(certificate);
            }
        }

        @Override
        public int getItemCount() {
            return mEstimateList.size() + mCertificateList.size();
        }
    }
}

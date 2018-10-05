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
import com.learningmachine.android.app.databinding.FragmentIssuerBinding;
import com.learningmachine.android.app.databinding.ListCertificateHeaderBinding;
import com.learningmachine.android.app.databinding.ListItemCertificateBinding;
import com.learningmachine.android.app.ui.LMFragment;
import com.learningmachine.android.app.ui.cert.AddCertificateActivity;
import com.learningmachine.android.app.util.DialogUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class IssuerFragment extends LMFragment {

    private static final String ARG_ISSUER_UUID = "IssuerFragment.IssuerUuid";

    @Inject
    IssuerManager mIssuerManager;
    @Inject protected CertificateManager mCertificateManager;

    private String mIssuerUuid;
    private IssuerRecord mIssuer;
    private FragmentIssuerBinding mBinding;
    private List<CertificateRecord> mCertificateList;

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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_issuer, container, false);

        mBinding.addCertificateFloatingActionButton.setOnClickListener(v -> {
            Timber.i("Add certificate button tapped");
            DialogUtils.showCustomSheet(getContext(), this,
                    R.layout.dialog_add_by_file_or_url,
                    0,
                    "",
                    "",
                    "",
                    "",
                    (btnIdx) -> {

                        if ((int)btnIdx == 0) {
                            Timber.i("Add Credential from URL tapped in issuer view");
                        } else {
                            Timber.i("User has chosen to add a certificate from file");
                        }
                        Intent intent = AddCertificateActivity.newIntent(getContext(), (int)btnIdx, null);
                        startActivity(intent);
                        return null;
                    },
                    (dialogContent) -> {
                        return null;
                    });
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
                Timber.i("More info tapped on the Issuer display");
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

        mIssuerManager.getIssuer(mIssuerUuid).compose(bindToMainThread())
                .subscribe((issuerRecord) -> {
                    this.mIssuer = issuerRecord;
                    updateRecyclerView(null);
                }, throwable -> Timber.e(throwable, "Unable to load certificates"));
    }

    private void setupRecyclerView() {
        CertificateAdapter adapter = new CertificateAdapter(mCertificateList);
        mBinding.certificateRecyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mBinding.certificateRecyclerView.setLayoutManager(layoutManager);
    }

    private void updateRecyclerView(List<CertificateRecord> certificateList) {
        if(certificateList != null) {
            mCertificateList.clear();
            mCertificateList.addAll(certificateList);
        }

        if(mIssuer != null) {
            mIssuer.cachedNumberOfCertificatesForIssuer = mCertificateList.size();

            mBinding.certificateRecyclerView.getAdapter()
                    .notifyDataSetChanged();

            boolean emptyCertificates = mCertificateList.isEmpty();
            mBinding.certificateMainContent.setVisibility(View.VISIBLE);
            mBinding.certificateEmptyContent.setVisibility(emptyCertificates ? View.VISIBLE : View.GONE);
        }
    }

    private class CertificateAdapter extends RecyclerView.Adapter {

        private List<CertificateRecord> mCertificateList;

        CertificateAdapter(List<CertificateRecord> certificateList) {
            mCertificateList = certificateList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == 0) {
                Context context = parent.getContext();
                LayoutInflater inflater = LayoutInflater.from(context);
                ListCertificateHeaderBinding binding = DataBindingUtil.inflate(inflater,
                        R.layout.list_certificate_header,
                        parent,
                        false);
                return new CertificateHeaderViewHolder(binding);

            }

            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            ListItemCertificateBinding binding = DataBindingUtil.inflate(inflater,
                    R.layout.list_item_certificate,
                    parent,
                    false);
            return new CertificateViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int viewType = holder.getItemViewType();
            if (viewType == 0){
                ((CertificateHeaderViewHolder)holder).bind(mIssuer);
            }
            if (viewType == 1){
                CertificateRecord certificate = mCertificateList.get(position - 1);
                ((CertificateViewHolder)holder).bind(certificate);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if(position == 0) {
                return 0;
            }
            return 1;
        }

        @Override
        public int getItemCount() {
            return mCertificateList.size() + 1;
        }
    }
}

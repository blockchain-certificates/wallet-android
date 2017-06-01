package com.learningmachine.android.app.ui.cert;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.CertificateManager;
import com.learningmachine.android.app.data.CertificateVerifier;
import com.learningmachine.android.app.data.IssuerManager;
import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.cert.metadata.Field;
import com.learningmachine.android.app.data.cert.metadata.MetaData;
import com.learningmachine.android.app.data.cert.metadata.MetaDataTypeAdapter;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.data.model.CertificateRecord;
import com.learningmachine.android.app.databinding.CertificateInfoItemBinding;
import com.learningmachine.android.app.databinding.FragmentCertificateInfoBinding;
import com.learningmachine.android.app.dialog.AlertDialogFragment;
import com.learningmachine.android.app.ui.LMFragment;
import com.learningmachine.android.app.ui.issuer.IssuerActivity;
import com.learningmachine.android.app.util.StringUtils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class CertificateInfoFragment extends LMFragment {

    private static final String ARG_CERTIFICATE_UUID = "CertificateInfoFragment.CertificateUuid";
    private static final int DELETE_CONFIRMATION_REQUEST_CODE = 999;

    @Inject CertificateManager mCertificateManager;
    @Inject IssuerManager mIssuerManager;
    @Inject CertificateVerifier mCertificateVerifier;

    private FragmentCertificateInfoBinding mBinding;
    private CertificateRecord mCertificate;

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

        mBinding.certificateInfoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        String certificateUuid = getArguments().getString(ARG_CERTIFICATE_UUID);
        mCertificateVerifier.loadCertificate(certificateUuid)
                .compose(bindToMainThread())
                .subscribe(blockCert -> mBinding.certificateInfoRecyclerView.setAdapter(new CertificateInfoAdapter(blockCert)),
                        throwable -> Timber.e(throwable, "Unable to load certificate & issuer"));

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
                displayAlert(DELETE_CONFIRMATION_REQUEST_CODE,
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
        if (requestCode == DELETE_CONFIRMATION_REQUEST_CODE && resultCode == AlertDialogFragment.RESULT_POSITIVE) {
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

    private class CertificateInfoAdapter extends RecyclerView.Adapter<CertificateInfoItemViewHolder> {

        private final List<CertificateInfoItemViewModel> mViewModels;

        CertificateInfoAdapter(BlockCert blockCert) {
            List<CertificateInfoItemViewModel> viewModels = parseMetaData(blockCert);
            viewModels.add(0, new CertificateInfoItemViewModel(getString(R.string.fragment_certificate_info_issuer_title), blockCert.getIssuer().getName()));
            viewModels.add(1, new CertificateInfoItemViewModel(getString(R.string.fragment_certificate_info_issuer_issue_date), blockCert.getIssueDate()));
            this.mViewModels = viewModels;
        }

        @NonNull
        private List<CertificateInfoItemViewModel> parseMetaData(BlockCert blockCert) {
            List<CertificateInfoItemViewModel> viewModels = new ArrayList<>();
            String metaDataString = blockCert.getMetaData();
            if (StringUtils.isEmpty(metaDataString)) {
                return viewModels;
            }
            NumberFormat numberFormat = NumberFormat.getInstance();
            NumberFormat integerFormat = NumberFormat.getIntegerInstance();
            MetaDataTypeAdapter typeAdapter = new MetaDataTypeAdapter(numberFormat, integerFormat, "True", "False");
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(MetaData.class, typeAdapter)
                    .create();
            MetaData metaData = gson.fromJson(metaDataString, MetaData.class);
            for (Field field : metaData.getFields()) {
                viewModels.add(new CertificateInfoItemViewModel(field.getTitle(), field.getValue()));
            }
            return viewModels;
        }

        @Override
        public CertificateInfoItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            CertificateInfoItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.certificate_info_item, parent, false);
            return new CertificateInfoItemViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(CertificateInfoItemViewHolder holder, int position) {
            CertificateInfoItemViewModel viewModel = mViewModels.get(position);
            holder.bind(viewModel);
        }

        @Override
        public int getItemCount() {
            return mViewModels.size();
        }
    }
}

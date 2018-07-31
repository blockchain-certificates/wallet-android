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

import com.google.gson.JsonParseException;
import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.CertificateManager;
import com.learningmachine.android.app.data.IssuerManager;
import com.learningmachine.android.app.data.cert.metadata.Field;
import com.learningmachine.android.app.data.cert.metadata.Metadata;
import com.learningmachine.android.app.data.cert.metadata.MetadataParser;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.data.model.CertificateRecord;
import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.databinding.CertificateInfoItemBinding;
import com.learningmachine.android.app.databinding.FragmentCertificateInfoBinding;
import com.learningmachine.android.app.dialog.AlertDialogFragment;
import com.learningmachine.android.app.ui.LMActivity;
import com.learningmachine.android.app.ui.LMFragment;
import com.learningmachine.android.app.ui.home.HomeActivity;
import com.learningmachine.android.app.ui.issuer.IssuerActivity;
import com.learningmachine.android.app.util.DateUtils;
import com.learningmachine.android.app.util.DialogUtils;
import com.learningmachine.android.app.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class CertificateInfoFragment extends LMFragment {

    private static final String ARG_CERTIFICATE_UUID = "CertificateInfoFragment.CertificateUuid";
    private static final int DELETE_CONFIRMATION_REQUEST_CODE = 999;

    @Inject CertificateManager mCertificateManager;
    @Inject IssuerManager mIssuerManager;

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

        mCertificateManager.getCertificate(certificateUuid)
                .flatMap(certificate -> {
                    mCertificate = certificate;
                    String issuerUuid = certificate.getIssuerUuid();
                    return mIssuerManager.getIssuer(issuerUuid);
                })
                .subscribe(issuer -> {
                    CertificateInfoAdapter adapter = new CertificateInfoAdapter(this, mCertificate, issuer);
                    mBinding.certificateInfoRecyclerView.setAdapter(adapter);
                }, throwable -> Timber.e(throwable, "Unable to load certificate & issuer"));

        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_certificate_info, menu);
    }

    private class CertificateInfoAdapter extends RecyclerView.Adapter<CertificateInfoItemViewHolder> {

        private final List<CertificateInfoItemViewModel> mViewModels;

        CertificateInfoAdapter(CertificateInfoFragment fragment, CertificateRecord certificate, IssuerRecord issuer) {
            List<CertificateInfoItemViewModel> viewModels = getHeaderData(certificate, issuer);
            List<CertificateInfoItemViewModel> metadataViewModels = getMetadata(certificate);
            viewModels.addAll(metadataViewModels);

            CertificateInfoItemViewModel deleteButton = new CertificateInfoItemViewModel("", "");
            deleteButton.setIsDeleteButton(() -> {
                Timber.i("User has tapped the delete button on this certificate");
                DialogUtils.showAlertDialog(getContext(), fragment,
                        0,
                        getResources().getString(R.string.fragment_certificate_info_delete_warning_title),
                        getResources().getString(R.string.fragment_certificate_info_delete_warning_message),
                        getResources().getString(R.string.fragment_certificate_info_delete_warning_positive_title),
                        getResources().getString(R.string.fragment_certificate_info_delete_warning_negative_title),
                        (btnIdx) -> {
                            if((int)btnIdx == 0) {
                                String uuid = mCertificate.getUuid();
                                mCertificateManager.removeCertificate(uuid)
                                        .compose(bindToMainThread())
                                        .subscribe(success -> {
                                            if (success) {
                                                Timber.i(String.format("User has deleted certificate %s with id %s",
                                                        mCertificate.getName(), mCertificate.getUuid()));
                                            } else {
                                                Timber.e(String.format("Deleting certificate %s failed", mCertificate.getUuid()));
                                            }
                                            ((LMActivity)getActivity()).safeGoBack();
                                        });
                            } else {
                                Timber.i("User canceled the deletion of the certificate");
                            }
                            return null;
                        });

                return null;
            });

            viewModels.add(deleteButton);

            this.mViewModels = viewModels;
        }

        private List<CertificateInfoItemViewModel> getHeaderData(CertificateRecord certificate, IssuerRecord issuer) {
            String issuerTitle = getString(R.string.fragment_certificate_info_issuer_title);
            String issuerName = issuer.getName();
            CertificateInfoItemViewModel issuerViewModel = new CertificateInfoItemViewModel(issuerTitle, issuerName);

            String dateString = certificate.getIssuedOn();
            String issueDate = DateUtils.formatDateString(dateString);
            String issueDateTitle = getString(R.string.fragment_certificate_info_issuer_issue_date);
            CertificateInfoItemViewModel issueDateViewModel = new CertificateInfoItemViewModel(issueDateTitle, issueDate);

            List<CertificateInfoItemViewModel> viewModels = new ArrayList<>();
            viewModels.add(issuerViewModel);
            viewModels.add(issueDateViewModel);
            return viewModels;
        }

        @NonNull
        private List<CertificateInfoItemViewModel> getMetadata(CertificateRecord certificate) {
            List<CertificateInfoItemViewModel> viewModels = new ArrayList<>();
            String metadataString = certificate.getMetadata();
            if (StringUtils.isEmpty(metadataString)) {
                return viewModels;
            }
            MetadataParser metadataParser = new MetadataParser(getContext());
            try {
                Metadata metadata = metadataParser.fromJson(metadataString);
                for (Field field : metadata.getFields()) {
                    viewModels.add(new CertificateInfoItemViewModel(field.getTitle(), field.getValue()));
                }
            } catch (JsonParseException e) {
                Timber.e(e, "Malformed metadata JSON");
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

package com.hyland.android.app.ui.issuer;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.hyland.android.app.data.model.CertificateRecord;
import com.hyland.android.app.databinding.ListItemCertificateBinding;
import com.hyland.android.app.ui.cert.CertificateActivity;

import timber.log.Timber;

public class CertificateViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ListItemCertificateBinding mBinding;
    private CertificateListItemViewModel mViewModel;

    public CertificateViewHolder(ListItemCertificateBinding binding) {
        super(binding.getRoot());

        mBinding = binding;
        mBinding.getRoot()
                .setOnClickListener(this);

        mViewModel = new CertificateListItemViewModel();
        mBinding.setViewModel(mViewModel);
    }

    @Override
    public void onClick(View v) {
        CertificateRecord certificate = mViewModel.getCertificate();
        Timber.i("Navigating to certificate " + certificate.getName());
        Context context = mBinding.getRoot()
                .getContext();
        String certUuid = certificate.getUuid();
        Intent intent = CertificateActivity.newIntent(context, certUuid);
        context.startActivity(intent);
    }

    public void bind(CertificateRecord certificate) {
        mViewModel.bindCertificate(certificate);
        mBinding.executePendingBindings();
    }
}

package com.learningmachine.android.app.ui.issuer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.learningmachine.android.app.data.model.CertificateRecord;
import com.learningmachine.android.app.data.model.IssuingEstimate;
import com.learningmachine.android.app.databinding.ListItemCertificateBinding;
import com.learningmachine.android.app.ui.cert.CertificateActivity;

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
        if (certificate == null) {
            return;
        }

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

    public void bind(IssuingEstimate estimate) {
        mViewModel.bindEstimate(estimate);
        mBinding.executePendingBindings();
    }
}

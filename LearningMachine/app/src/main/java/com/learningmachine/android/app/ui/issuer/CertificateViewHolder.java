package com.learningmachine.android.app.ui.issuer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.learningmachine.android.app.data.model.Certificate;
import com.learningmachine.android.app.databinding.ListItemCertificateBinding;
import com.learningmachine.android.app.ui.cert.CertificateActivity;

public class CertificateViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {

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
        Certificate certificate = mViewModel.getCertificate();
        Context context = mBinding.getRoot().getContext();
        Intent intent = CertificateActivity.newIntent(context, certificate);
        context.startActivity(intent);
    }

    public void bind(Certificate certificate) {
        mViewModel.bindCertificate(certificate);
        mBinding.executePendingBindings();
    }
}

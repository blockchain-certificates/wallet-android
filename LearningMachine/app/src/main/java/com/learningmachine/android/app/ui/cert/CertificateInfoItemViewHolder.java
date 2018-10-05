package com.learningmachine.android.app.ui.cert;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.databinding.CertificateInfoItemBinding;
import com.learningmachine.android.app.ui.issuer.IssuerActivity;
import com.learningmachine.android.app.util.DialogUtils;

public class CertificateInfoItemViewHolder extends RecyclerView.ViewHolder {
    private final CertificateInfoItemBinding mBinding;

    public CertificateInfoItemViewHolder(CertificateInfoItemBinding binding) {
        super(binding.getRoot());
        mBinding = binding;
    }

    public void bind(CertificateInfoItemViewModel viewModel) {
        mBinding.setItem(viewModel);
    }
}

package com.learningmachine.android.app.ui.issuer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.model.CertificateRecord;
import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.databinding.ListCertificateHeaderBinding;
import com.learningmachine.android.app.databinding.ListItemCertificateBinding;
import com.learningmachine.android.app.ui.cert.CertificateActivity;
import com.learningmachine.android.app.util.ImageUtils;
import com.squareup.picasso.Picasso;

import java.io.File;

public class CertificateHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context mContext;
    private ListCertificateHeaderBinding mBinding;
    private CertificateHeaderViewModel mViewModel;

    public CertificateHeaderViewHolder(ListCertificateHeaderBinding binding) {
        super(binding.getRoot());

        mBinding = binding;
        mBinding.getRoot()
                .setOnClickListener(this);

        mContext = mBinding.getRoot()
                .getContext();

        mViewModel = new CertificateHeaderViewModel();
        mBinding.setViewModel(mViewModel);
    }

    @Override
    public void onClick(View v) {
        // TODO: Open the issuer information view?
    }

    public void bind(IssuerRecord issuer) {
        mViewModel.bindIssuer(issuer);
        loadImageView(issuer);
        mBinding.executePendingBindings();
    }

    private void loadImageView(IssuerRecord issuer) {
        if (issuer != null) {
            String uuid = issuer.getUuid();
            File file = ImageUtils.getImageFile(mContext, uuid);

            Picasso.with(mContext)
                    .load(file)
                    .placeholder(R.mipmap.ic_placeholder)
                    .into(mBinding.imageView);
        }
    }
}

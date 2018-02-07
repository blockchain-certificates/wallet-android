package com.learningmachine.android.app.ui.home;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.databinding.ListItemIssuerBinding;
import com.learningmachine.android.app.ui.issuer.IssuerActivity;
import com.learningmachine.android.app.util.ImageUtils;
import com.squareup.picasso.Picasso;

import java.io.File;

public class IssuerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context mContext;
    private ListItemIssuerBinding mBinding;
    private IssuerListItemViewModel mViewModel;

    public IssuerViewHolder(ListItemIssuerBinding binding) {
        super(binding.getRoot());

        mBinding = binding;
        mBinding.getRoot()
                .setOnClickListener(this);

        mContext = mBinding.getRoot()
                .getContext();

        mViewModel = new IssuerListItemViewModel();
        mBinding.setViewModel(mViewModel);
    }

    @Override
    public void onClick(View v) {
        IssuerRecord issuer = mViewModel.getIssuer();
        String issuerUuid = issuer.getUuid();
        Intent intent = IssuerActivity.newIntent(mContext, issuerUuid);
        mContext.startActivity(intent);
    }

    public void bind(IssuerRecord issuer) {
        mViewModel.bindIssuer(issuer);
        loadImageView(issuer);
        mBinding.executePendingBindings();
    }

    private void loadImageView(IssuerRecord issuer) {
        String uuid = issuer.getUuid();
        File file = ImageUtils.getImageFile(mContext, uuid);

        Picasso.with(mContext)
                .load(file)
                .placeholder(R.mipmap.ic_placeholder)
                .into(mBinding.imageView);
    }
}
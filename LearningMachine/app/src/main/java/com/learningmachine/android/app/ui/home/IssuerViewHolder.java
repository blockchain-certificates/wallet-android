package com.learningmachine.android.app.ui.home;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.learningmachine.android.app.data.model.Issuer;
import com.learningmachine.android.app.databinding.ListItemIssuerBinding;
import com.learningmachine.android.app.ui.issuer.IssuerActivity;
import com.learningmachine.android.app.util.ImageUtils;
import com.learningmachine.android.app.util.StringUtils;
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
        Issuer issuer = mViewModel.getIssuer();
        Intent intent = IssuerActivity.newIntent(mContext, issuer);
        mContext.startActivity(intent);
    }

    public void bind(Issuer issuer) {
        mViewModel.bindIssuer(issuer);
        loadImageView(issuer);
        mBinding.executePendingBindings();
    }

    private void loadImageView(Issuer issuer) {
        String uuid = issuer.getUuid();
        File file = ImageUtils.getImageFile(mContext, uuid);

        Picasso.with(mContext)
                .load(file)
                .into(mBinding.imageView);
    }
}
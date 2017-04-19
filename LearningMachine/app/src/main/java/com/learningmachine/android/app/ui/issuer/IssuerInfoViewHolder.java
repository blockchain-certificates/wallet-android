package com.learningmachine.android.app.ui.issuer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.learningmachine.android.app.data.model.IssuerInfo;
import com.learningmachine.android.app.databinding.FragmentIssuerInfoBinding;

public class IssuerInfoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private FragmentIssuerInfoBinding mInfoBinding;
    private IssuerInfoViewModel mIssuerInfoViewModel;

    public IssuerInfoViewHolder(FragmentIssuerInfoBinding binding) {
        super(binding.getRoot());

        mInfoBinding = binding;
        mInfoBinding.getRoot().setOnClickListener(this);

//        mIssuerInfoViewModel = new IssuerInfoViewModel();
//        mInfoBinding.setViewModel(mIssuerInfoViewModel);
    }

    @Override
    public void onClick(View v) {
        IssuerInfo issuerInfo = mIssuerInfoViewModel.getIssuerInfo();
        Context context = mInfoBinding.getRoot().getContext();

        Intent intent = IssuerInfoActivity.newIntent(context, issuerInfo);
        context.startActivity(intent);

    }

    public void bind(IssuerInfo info) {
        mIssuerInfoViewModel.bindIssuerInfo(info);
        mInfoBinding.executePendingBindings();
    }
}

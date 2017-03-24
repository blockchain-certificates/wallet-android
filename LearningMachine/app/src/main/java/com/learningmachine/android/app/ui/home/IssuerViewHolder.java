package com.learningmachine.android.app.ui.home;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

import com.learningmachine.android.app.databinding.ListItemIssuerBinding;

public class IssuerViewHolder extends RecyclerView.ViewHolder {

    private IssuerListItemViewModel mViewModel;

    public IssuerViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());

        Context context = binding.getRoot()
                .getContext();
        mViewModel = new IssuerListItemViewModel(context);

        ((ListItemIssuerBinding) binding).setViewModel(mViewModel);
    }

    public IssuerListItemViewModel getViewModel() {
        return mViewModel;
    }
}
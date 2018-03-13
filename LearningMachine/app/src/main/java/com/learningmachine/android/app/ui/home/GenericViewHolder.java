package com.learningmachine.android.app.ui.home;

import android.content.Context;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.databinding.ListItemIssuerBinding;
import com.learningmachine.android.app.ui.issuer.IssuerActivity;
import com.learningmachine.android.app.util.ImageUtils;
import com.squareup.picasso.Picasso;

import java.io.File;

public class GenericViewHolder extends RecyclerView.ViewHolder {

    public GenericViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
    }
}
package com.learningmachine.android.app.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.databinding.ListItemIssuerBinding;
import com.learningmachine.android.app.ui.issuer.IssuerActivity;
import com.learningmachine.android.app.util.ImageUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

import timber.log.Timber;

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
        Timber.i(String.format("Navigating to issuer %s with id: %s", issuer.getName(), issuerUuid));
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

        mBinding.imageView.setBackgroundResource(R.color.white);

        if (file != null) {
            Picasso.with(mContext).load(file).fetch(new Callback() {
                @Override
                public void onSuccess() {

                    Picasso.with(mContext).load(file).into(mBinding.imageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            //do smth when picture is loaded successfully
                            BitmapDrawable drawable = (BitmapDrawable) mBinding.imageView.getDrawable();
                            if (drawable != null) {
                                Bitmap bitmap = drawable.getBitmap();
                                int pixel = bitmap.getPixel(bitmap.getWidth() - 1, 0);
                                if (!ImageUtils.hasTransparentPixel(bitmap)) {
                                    mBinding.imageView.setBackgroundColor(pixel);
                                }
                            }
                        }

                        @Override
                        public void onError() {
                            //do smth when there is picture loading error
                        }
                    });
                }

                @Override
                public void onError() {

                }
            });
        }
    }
}
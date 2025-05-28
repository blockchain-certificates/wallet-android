package com.hyland.android.app.ui.issuer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.hyland.android.app.data.model.IssuerRecord;
import com.hyland.android.app.databinding.ListCertificateHeaderBinding;
import com.hyland.android.app.util.ImageUtils;
import com.squareup.picasso.Callback;
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

            if(file != null) {
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
}

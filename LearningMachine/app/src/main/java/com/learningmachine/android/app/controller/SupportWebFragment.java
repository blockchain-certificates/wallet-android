package com.learningmachine.android.app.controller;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewClient;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.databinding.ActivitySupportWebBinding;
import com.learningmachine.android.app.ui.LMFragment;

public abstract class SupportWebFragment extends LMFragment {

    protected ActivitySupportWebBinding mBinding;

    public SupportWebFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.activity_support_web, container, false);
        this.setupWebView();
        this.chooseWebsite();

        return mBinding.getRoot();
    }

    protected void setupWebView() {
        WebViewClient webViewClient = new WebViewClient();
        mBinding.baseWebView.setWebViewClient(webViewClient);
    }

    public void backPressed() {
        if (this.mBinding.baseWebView.canGoBack()) {
            this.mBinding.baseWebView.goBack();
        } else {
            this.getActivity()
                    .finish();
        }
    }

    private void chooseWebsite() {
        String endPoint = this.getEndPoint();
        if (endPoint != null && !endPoint.isEmpty()) {
            this.mBinding.baseWebView.loadUrl(endPoint);
        }
    }

    protected abstract String getEndPoint();
}

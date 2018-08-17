package com.learningmachine.android.app.ui;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.databinding.FragmentWebBinding;

public class LMWebFragment extends LMFragment {

    private static final String ARG_END_POINT = "LMWebFragment.EndPoint";

    protected FragmentWebBinding mBinding;

    public static LMWebFragment newInstance(String endPoint) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_END_POINT, endPoint);
        LMWebFragment fragment = new LMWebFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_web, container, false);
        setupWebView();
        loadWebsite();

        return mBinding.getRoot();
    }

    protected void setupWebView() {
        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mBinding.progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mBinding.progressBar.setVisibility(View.GONE);
            }
        };
        mBinding.webViewController.setWebViewClient(webViewClient);
        mBinding.webViewController.getSettings()
                .setJavaScriptEnabled(true);
    }

    public void backPressed() {
        if (mBinding.webViewController.canGoBack()) {
            mBinding.webViewController.goBack();
        } else {
            getActivity().finish();
        }
    }

    private void loadWebsite() {
        String endPoint = getEndPoint();
        if (endPoint != null && !TextUtils.isEmpty(endPoint)) {
            mBinding.webViewController.loadUrl(endPoint);
        }
    }

    protected String getEndPoint() {
        return getArguments().getString(ARG_END_POINT);
    }
}

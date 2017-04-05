package com.learningmachine.android.app.controller;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewClient;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.databinding.ActivitySupportWebBinding;
import com.learningmachine.android.app.ui.LMFragment;

public class LMWebFragment extends LMFragment {

    private static final String ARG_END_POINT = "LMWebFragment.EndPoint";

    protected ActivitySupportWebBinding mBinding;

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
        WebViewClient webViewClient = new WebViewClient();
        mBinding.baseWebView.setWebViewClient(webViewClient);
        mBinding.baseWebView.getSettings()
                .setJavaScriptEnabled(true);

    }

    public void backPressed() {
        if (mBinding.baseWebView.canGoBack()) {
            mBinding.baseWebView.goBack();
        } else {
            getActivity().finish();
        }
    }

    private void loadWebsite() {
        String endPoint = getEndPoint();
        if (endPoint != null && TextUtils.isEmpty(endPoint)) {
            mBinding.baseWebView.loadUrl(endPoint);
        }
    }

    protected String getEndPoint() {
        return getArguments().getString(ARG_END_POINT);
    }
}

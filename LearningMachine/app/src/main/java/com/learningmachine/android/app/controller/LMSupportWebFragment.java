package com.learningmachine.android.app.controller;

import android.os.Bundle;

public class LMSupportWebFragment extends SupportWebFragment {


    private static final String ARG_END_POINT = "LMSupportWebFragment.EndPoint";

    public static LMSupportWebFragment newInstance(String endPoint) {
        Bundle args =  new Bundle();
        args.putSerializable(ARG_END_POINT, endPoint);
        LMSupportWebFragment fragment = new LMSupportWebFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void setupWebView() {
        super.setupWebView();
        mBinding.baseWebView.getSettings().setJavaScriptEnabled(true);

    }

    @Override
    protected String getEndPoint() {
        return getArguments().getString(ARG_END_POINT);
    }
}

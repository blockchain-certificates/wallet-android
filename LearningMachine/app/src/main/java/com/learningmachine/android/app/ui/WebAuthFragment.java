package com.learningmachine.android.app.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.databinding.FragmentWebBinding;
import com.learningmachine.android.app.util.StringUtils;

public class WebAuthFragment extends LMFragment {

    private static final String ARG_END_POINT = "WebAuthFragment.EndPoint";
    private static final String ARG_SUCCESS_URL = "WebAuthFragment.SuccessURL";
    private static final String ARG_ERROR_URL = "WebAuthFragment.ErrorURL";

    protected FragmentWebBinding mBinding;
    private String mEndPoint;
    private String mSuccessUrlString;
    private String mErrorUrlString;
    private WebAuthCallbacks mCallbacks;

    public interface WebAuthCallbacks {
        void onSuccess();
        void onError();
    }

    public static WebAuthFragment newInstance(String endPoint, String successUrlString, String errorUrlString) {
        Bundle args = new Bundle();
        args.putString(ARG_END_POINT, endPoint);
        args.putString(ARG_SUCCESS_URL, successUrlString);
        args.putString(ARG_ERROR_URL, errorUrlString);
        WebAuthFragment fragment = new WebAuthFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEndPoint = getArguments().getString(ARG_END_POINT);
        mSuccessUrlString = getArguments().getString(ARG_SUCCESS_URL);
        mErrorUrlString = getArguments().getString(ARG_ERROR_URL);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WebAuthCallbacks) {
            mCallbacks = (WebAuthCallbacks) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_web, container, false);
        setupWebView();
        loadWebsite();

        return mBinding.getRoot();
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected void setupWebView() {
        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (mCallbacks != null && url.startsWith(mSuccessUrlString)) {
                    mCallbacks.onSuccess();
                    return true;
                } else if (mCallbacks != null && url.startsWith(mErrorUrlString)) {
                    mCallbacks.onError();
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
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
        if (StringUtils.isEmpty(mEndPoint)) {
            return;
        }
        mBinding.webViewController.loadUrl(mEndPoint);
    }
}

package com.learningmachine.android.app.ui.cert;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.data.model.Certificate;
import com.learningmachine.android.app.data.store.CertificateStore;
import com.learningmachine.android.app.databinding.FragmentCertificateBinding;
import com.learningmachine.android.app.ui.LMFragment;

import javax.inject.Inject;

public class CertificateFragment extends LMFragment {

    private static final String ARG_CERTIFICATE = "CertificateFragment.Certificate";
    private static final String INDEX_FILE_PATH = "file:///android_asset/www/index.html";

    @Inject protected CertificateStore mCertificateStore;

    private Certificate mCertificate;
    private FragmentCertificateBinding mBinding;

    public static CertificateFragment newInstance(Certificate certificate) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CERTIFICATE, certificate);

        CertificateFragment fragment = new CertificateFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.obtain(getContext())
                .inject(this);

        mCertificate = (Certificate) getArguments().getSerializable(ARG_CERTIFICATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_certificate, container, false);

        setupWebView();

        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_certificate, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fragment_certificate_verify_menu_item:
                return true;
            case R.id.fragment_certificate_share_menu_item:
                return true;
            case R.id.fragment_certificate_info_menu_item:
                    Intent intent = CertificateInfoActivity.newIntent(getActivity(), mCertificate);
                    startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupWebView() {
        WebSettings webSettings = mBinding.webView.getSettings();
        // Enable JavaScript.
        webSettings.setJavaScriptEnabled(true);
        // Enable HTML Imports to be loaded from file://.
        webSettings.setAllowFileAccessFromFileURLs(true);
        // Ensure local links/redirects in WebView, not the browser.
        mBinding.webView.setWebViewClient(new LMWebViewClient());

        mBinding.webView.loadUrl(INDEX_FILE_PATH);
    }

    public class LMWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Handle local URLs
            if (Uri.parse(url)
                    .getHost()
                    .length() == 0) {
                return false;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // uuid is currently wrong, but will be fixed when certs are actually added & saved
            String certFilePath = mCertificateStore.getCertificateJsonFileUrl(mCertificate.getUuid());

            String javascript = String.format(
                    "javascript:(function() { document.getElementsByTagName('blockchain-certificate')[0].href='%1$s';})()",
                    certFilePath);
            mBinding.webView.loadUrl(javascript);
        }
    }
}

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
import com.learningmachine.android.app.data.CertificateManager;
import com.learningmachine.android.app.data.CertificateVerifier;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.data.model.Certificate;
import com.learningmachine.android.app.data.webservice.IssuerService;
import com.learningmachine.android.app.databinding.FragmentCertificateBinding;
import com.learningmachine.android.app.ui.LMFragment;
import com.learningmachine.android.app.util.FileUtils;

import java.io.File;

import javax.inject.Inject;

import timber.log.Timber;

public class CertificateFragment extends LMFragment {

    private static final String ARG_CERTIFICATE_UUID = "CertificateFragment.CertificateUuid";
    private static final String INDEX_FILE_PATH = "file:///android_asset/www/index.html";

    @Inject protected CertificateManager mCertificateManager;
    @Inject protected IssuerService mIssuerService;
    @Inject protected CertificateVerifier mCertificateVerifier;

    private FragmentCertificateBinding mBinding;
    private String mCertUuid;

    public static CertificateFragment newInstance(String certificateUuid) {
        Bundle args = new Bundle();
        args.putString(ARG_CERTIFICATE_UUID, certificateUuid);

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

        mCertUuid = getArguments().getString(ARG_CERTIFICATE_UUID);
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
                verifyCertificate();
                return true;
            case R.id.fragment_certificate_share_menu_item:
                return true;
            case R.id.fragment_certificate_info_menu_item:
                String certUuid = getArguments().getString(ARG_CERTIFICATE_UUID);
                Intent intent = CertificateInfoActivity.newIntent(getActivity(), certUuid);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void verifyCertificate() {
        mCertificateVerifier.loadCertificate(mCertUuid)
                .compose(bindToMainThread())
                .subscribe(result -> {
                    Timber.d("Successfully loaded certificate and document");
                    verifyIssuer(result.getCertificate(), result.getDocument());
                }, throwable -> {
                    Timber.d(throwable, "Error!");
                    displayErrors(throwable, R.string.error_title_message); // TODO: use correct error string
                });
    }

    private void verifyIssuer(Certificate certificate, String serializedDoc) {
        mCertificateVerifier.verifyIssuer(certificate)
                .compose(bindToMainThread())
                .subscribe(issuerKey -> {
                    verifyBitcoinTransactionRecord(certificate, serializedDoc);
                }, throwable -> {
                    Timber.d(throwable, "Error! Couldn't verify issuer");
                    displayErrors(throwable, R.string.error_title_message); // TODO: use correct error string
                });
    }

    private void verifyBitcoinTransactionRecord(Certificate certificate, String serializedDoc) {
        mCertificateVerifier.verifyBitcoinTransactionRecord(certificate)
                .compose(bindToMainThread())
                .subscribe(remoteHash -> {
                    // TODO: success
                    verifyJsonLd(remoteHash, serializedDoc);
                }, throwable -> {
                    Timber.d(throwable, "Error!");
                    displayErrors(throwable, R.string.error_title_message); // TODO: use correct error string
                });
    }

    private void verifyJsonLd(String remoteHash, String serializedDoc) {
        mCertificateVerifier.verifyJsonLd(remoteHash, serializedDoc)
                .compose(bindToMainThread())
                .subscribe(localHash -> {
                    Timber.d("Success!");
                    showSnackbar(getView(), R.string.certificate_verification_success);
                }, throwable -> {
                    Timber.d(throwable, "Error!");
                    displayErrors(throwable, R.string.error_title_message); // TODO: use correct error string
                });
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
            File certFile = FileUtils.getCertificateFile(getContext(), mCertUuid);
            String certFilePath = certFile.toString();

            String javascript = String.format(
                    "javascript:(function() { document.getElementsByTagName('blockchain-certificate')[0].href='%1$s';})()",
                    certFilePath);
            mBinding.webView.loadUrl(javascript);
        }
    }
}

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
import com.learningmachine.android.app.data.IssuerManager;
import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.data.model.CertificateRecord;
import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.databinding.FragmentCertificateBinding;
import com.learningmachine.android.app.dialog.AlertDialogFragment;
import com.learningmachine.android.app.ui.LMFragment;
import com.learningmachine.android.app.util.FileUtils;

import java.io.File;

import javax.inject.Inject;

import rx.Observable;
import timber.log.Timber;

public class CertificateFragment extends LMFragment {

    private static final String ARG_CERTIFICATE_UUID = "CertificateFragment.CertificateUuid";
    private static final String INDEX_FILE_PATH = "file:///android_asset/www/index.html";
    private static final int REQUEST_SHARE_METHOD = 234;
    private static final String QUERY_PARAM_JSON = "?format=json";

    @Inject protected CertificateManager mCertificateManager;
    @Inject protected IssuerManager mIssuerManager;
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
        mIssuerManager.certificateViewed(mCertUuid)
                .compose(bindToMainThread())
                .subscribe(aVoid -> Timber.d("Issuer analytics: Certificate viewed"),
                        throwable -> Timber.e(throwable, "Issuer has no analytics url."));
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
                showShareTypeDialog();
                return true;
            case R.id.fragment_certificate_info_menu_item:
                viewCertificateInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SHARE_METHOD) {
            boolean shareFile = resultCode == AlertDialogFragment.RESULT_NEGATIVE;
            shareCertificate(shareFile);
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    private void showShareTypeDialog() {
        displayAlert(REQUEST_SHARE_METHOD,
                R.string.fragment_certificate_share_message,
                R.string.fragment_certificate_share_url_button_title,
                R.string.fragment_certificate_share_file_button_title);
    }

    private void shareCertificate(boolean shareFile) {
        mIssuerManager.certificateShared(mCertUuid)
                .compose(bindToMainThread())
                .subscribe(aVoid -> Timber.d("Issuer analytics: Certificate shared"),
                        throwable -> Timber.e(throwable, "Issuer has no analytics url."));
        String certUuid = getArguments().getString(ARG_CERTIFICATE_UUID);
        Observable.combineLatest(mCertificateManager.getCertificate(certUuid),
                mIssuerManager.getIssuerForCertificate(certUuid),
                CertificateIssuerHolder::new)
                .compose(bindToMainThread())
                .subscribe(holder -> {
                    CertificateRecord cert = holder.getCertificate();
                    String certUrlString = cert.getUrlString();
                    if (shareFile) {
                        certUrlString += QUERY_PARAM_JSON;
                    }
                    IssuerRecord issuer = holder.getIssuer();
                    String issuerName = issuer.getName();
                    String sharingText = getString(R.string.fragment_certificate_share_format,
                            issuerName,
                            certUrlString);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, sharingText);
                    intent.setType("text/plain");
                    startActivity(intent);
                }, throwable -> Timber.e(throwable, "Unable to share certificate"));
    }

    private class CertificateIssuerHolder {
        private CertificateRecord mCertificate;
        private IssuerRecord mIssuer;

        public CertificateIssuerHolder(CertificateRecord certificate, IssuerRecord issuer) {
            mCertificate = certificate;
            mIssuer = issuer;
        }

        public CertificateRecord getCertificate() {
            return mCertificate;
        }

        public IssuerRecord getIssuer() {
            return mIssuer;
        }
    }

    private void viewCertificateInfo() {
        String certUuid = getArguments().getString(ARG_CERTIFICATE_UUID);
        Intent intent = CertificateInfoActivity.newIntent(getActivity(), certUuid);
        startActivity(intent);
    }

    private void verifyCertificate() {
        mIssuerManager.certificateVerified(mCertUuid)
                .compose(bindToMainThread())
                .subscribe(aVoid -> Timber.d("Issuer analytics: Certificate verified"),
                        throwable -> Timber.e(throwable, "Issuer has no analytics url."));
        mCertificateVerifier.loadCertificate(mCertUuid)
                .compose(bindToMainThread())
                .subscribe(certificateAndDocument -> {
                    Timber.d("Successfully loaded certificate and document");
                    verifyIssuer(certificateAndDocument.getCertificate(), certificateAndDocument.getDocument());
                }, throwable -> {
                    Timber.e(throwable, "Error!");
                    displayErrors(throwable, R.string.error_title_message); // TODO: use correct error string
                });
    }

    private void verifyIssuer(BlockCert certificate, String serializedDoc) {
        mCertificateVerifier.verifyIssuer(certificate)
                .compose(bindToMainThread())
                .subscribe(issuerKey -> {
                    verifyBitcoinTransactionRecord(certificate, serializedDoc);
                }, throwable -> {
                    Timber.e(throwable, "Error! Couldn't verify issuer");
                    displayErrors(throwable, R.string.error_title_message); // TODO: use correct error string
                });
    }

    private void verifyBitcoinTransactionRecord(BlockCert certificate, String serializedDoc) {
        mCertificateVerifier.verifyBitcoinTransactionRecord(certificate)
                .compose(bindToMainThread())
                .subscribe(remoteHash -> {
                    // TODO: success
                    verifyJsonLd(remoteHash, serializedDoc);
                }, throwable -> {
                    Timber.e(throwable, "Error!");
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
                    Timber.e(throwable, "Error!");
                    displayErrors(throwable, R.string.error_title_message); // TODO: use correct error string
                });
    }
}

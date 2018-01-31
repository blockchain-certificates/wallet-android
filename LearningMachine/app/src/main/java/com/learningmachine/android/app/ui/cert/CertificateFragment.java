package com.learningmachine.android.app.ui.cert;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Pair;
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
import com.learningmachine.android.app.data.CertificateVerifier.CertificateVerificationResult;
import com.learningmachine.android.app.data.CertificateVerifier.CertificateVerificationStatus;
import com.learningmachine.android.app.data.IssuerManager;
import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.error.ExceptionWithResourceString;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.data.model.CertificateRecord;
import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.data.model.TxRecord;
import com.learningmachine.android.app.databinding.FragmentCertificateBinding;
import com.learningmachine.android.app.dialog.AlertDialogFragment;
import com.learningmachine.android.app.dialog.CertificateVerficationProgressFragment;
import com.learningmachine.android.app.ui.LMFragment;
import com.learningmachine.android.app.util.DialogUtils;
import com.learningmachine.android.app.util.FileUtils;

import java.io.File;

import javax.inject.Inject;

import rx.Observable;
import timber.log.Timber;

import static com.learningmachine.android.app.dialog.CertificateVerficationProgressFragment.VerficationCancelListener;

public class CertificateFragment extends LMFragment implements VerficationCancelListener {

    private static final String ARG_CERTIFICATE_UUID = "CertificateFragment.CertificateUuid";
    private static final String TAG_VERIFICATION_PROGRESS_DIALOG = "CertificateFragment.CertificateVerficationProgressFragment";
    private static final String INDEX_FILE_PATH = "file:///android_asset/www/index.html";
    private static final String FILE_PROVIDER_AUTHORITY = "com.learningmachine.android.app.fileprovider";
    private static final String TEXT_MIME_TYPE = "text/plain";
    private static final int REQUEST_SHARE_METHOD = 234;

    @Inject protected CertificateManager mCertificateManager;
    @Inject protected IssuerManager mIssuerManager;
    @Inject protected CertificateVerifier mCertificateVerifier;

    private FragmentCertificateBinding mBinding;
    private String mCertUuid;
    private boolean mCancelVerification;

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
                shareCertificate();
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
            shareCertificateTypeResult(shareFile);
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

    private void shareCertificate() {
        String certUuid = getArguments().getString(ARG_CERTIFICATE_UUID);
        mCertificateManager.getCertificate(certUuid)
                .compose(bindToMainThread())
                .subscribe(certificateRecord -> {
                    if (certificateRecord.urlStringContainsUrl()) {
                        showShareTypeDialog();
                    } else {
                        shareCertificateTypeResult(true);
                    }
                }, throwable -> Timber.e(throwable, "Unable to share certificate"));
    }

    private void showShareTypeDialog() {
        displayAlert(REQUEST_SHARE_METHOD,
                R.string.fragment_certificate_share_message,
                R.string.fragment_certificate_share_url_button_title,
                R.string.fragment_certificate_share_file_button_title);
    }

    private void shareCertificateTypeResult(boolean shareFile) {
        mIssuerManager.certificateShared(mCertUuid)
                .compose(bindToMainThread())
                .subscribe(aVoid -> Timber.d("Issuer analytics: Certificate shared"),
                        throwable -> Timber.e(throwable, "Issuer has no analytics url."));
        Observable.combineLatest(mCertificateManager.getCertificate(mCertUuid),
                mIssuerManager.getIssuerForCertificate(mCertUuid),
                Pair::new)
                .compose(bindToMainThread())
                .subscribe(pair -> {
                    CertificateRecord cert = pair.first;

                    Intent intent = new Intent(Intent.ACTION_SEND);

                    IssuerRecord issuer = pair.second;
                    String issuerName = issuer.getName();

                    String sharingText;

                    if (shareFile) {
                        File certFile = FileUtils.getCertificateFile(getContext(), mCertUuid);
                        Uri uri = FileProvider.getUriForFile(getContext(), FILE_PROVIDER_AUTHORITY, certFile);
                        String type = getContext().getContentResolver()
                                .getType(uri);
                        intent.setType(type);
                        intent.putExtra(Intent.EXTRA_STREAM, uri);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        sharingText = getString(R.string.fragment_certificate_share_file_format, issuerName);
                    } else {
                        intent.setType(TEXT_MIME_TYPE);
                        String certUrlString = cert.getUrlString();
                        sharingText = getString(R.string.fragment_certificate_share_url_format,
                                issuerName,
                                certUrlString);
                    }

                    intent.putExtra(Intent.EXTRA_TEXT, sharingText);
                    startActivity(intent);
                }, throwable -> Timber.e(throwable, "Unable to share certificate"));
    }

    private void viewCertificateInfo() {
        Intent intent = CertificateInfoActivity.newIntent(getActivity(), mCertUuid);
        startActivity(intent);
    }

    private void showVerficationProgressDialog() {
        CertificateVerficationProgressFragment fragment = CertificateVerficationProgressFragment.newInstance();
        fragment.show(getFragmentManager(), TAG_VERIFICATION_PROGRESS_DIALOG);
        fragment.setCancelClickListener(this);
    }

    private void updateVerficationProgressDialog(CertificateVerificationStatus status) {
        Fragment fragment = getFragmentManager().findFragmentByTag(TAG_VERIFICATION_PROGRESS_DIALOG);
        if (fragment instanceof CertificateVerficationProgressFragment) {
            ((CertificateVerficationProgressFragment) fragment).updateVerificationStatus(status);
        }
    }

    private void hideVerificationProgressDialog() {
        Fragment fragment = getFragmentManager().findFragmentByTag(TAG_VERIFICATION_PROGRESS_DIALOG);
        if (fragment instanceof DialogFragment) {
            ((DialogFragment) fragment).dismissAllowingStateLoss();
        }
    }

    private void showVerificationResultDialog(CertificateVerificationResult status) {
        hideVerificationProgressDialog();
        displayAlert(0,
                status.getTitleResId(),
                status.getMessageResId(),
                R.string.dialog_verify_cert_result_positive_button_title,
                0);
    }

    private void showVerificationFailureDialog(int errorId) {
        hideVerificationProgressDialog();
        displayAlert(0,
                R.string.cert_verification_failure_title,
                errorId,
                R.string.dialog_verify_cert_result_positive_button_title,
                0);
    }

    @Override
    public void onVerificationCancelClick() {
        mCancelVerification = true;
    }

    private void verifyCertificate() {
        mCancelVerification = false;
        showVerficationProgressDialog();



        mCertificateVerifier.getUpdates()
                .subscribe(this::updateVerficationProgressDialog);

        mIssuerManager.certificateVerified(mCertUuid)
                .compose(bindToMainThread())
                .subscribe(aVoid -> Timber.d("Issuer analytics: Certificate verified"),
                        throwable -> Timber.e(throwable, "Issuer has no analytics url."));

        mCertificateVerifier.loadCertificate(mCertUuid)
                .compose(bindToMainThread())
                .subscribe(certificate -> {
                    Timber.d("Successfully loaded certificate");
                    verifyBitcoinTransactionRecord(certificate);
                }, throwable -> {
                    Timber.e(throwable, "Error!");

                    ExceptionWithResourceString throwableRS = (ExceptionWithResourceString)throwable;
                    showVerificationFailureDialog(throwableRS.getErrorMessageResId());
                });
    }

    private void verifyBitcoinTransactionRecord(BlockCert certificate) {
        if (mCancelVerification) {
            return;
        }

        mCertificateVerifier.verifyBitcoinTransactionRecord(certificate)
                .compose(bindToMainThread())
                .subscribe(txRecord -> {
                    // TODO: success
                    verifyIssuer(certificate, txRecord);
                }, throwable -> {
                    Timber.e(throwable, "Error! Merkle roots do not match");

                    ExceptionWithResourceString throwableRS = (ExceptionWithResourceString)throwable;
                    showVerificationFailureDialog(throwableRS.getErrorMessageResId());
                });
    }

    private void verifyIssuer(BlockCert certificate, TxRecord txRecord) {
        if (mCancelVerification) {
            return;
        }

        mCertificateVerifier.verifyIssuer(certificate, txRecord)
                .compose(bindToMainThread())
                .subscribe(issuerResponse -> verifyJsonLd(certificate, txRecord), throwable -> {
                    Timber.e(throwable, "Error! Couldn't verify issuer");

                    ExceptionWithResourceString throwableRS = (ExceptionWithResourceString)throwable;
                    showVerificationFailureDialog(throwableRS.getErrorMessageResId());
                });
    }

    private void verifyJsonLd(BlockCert certificate, TxRecord txRecord) {
        if (mCancelVerification) {
            return;
        }

        mCertificateVerifier.verifyJsonLd(certificate, txRecord)
                .compose(bindToMainThread())
                .subscribe(localHash -> {
                    Timber.d("Success!");
                    showVerificationResultDialog(CertificateVerificationResult.VALID_CERT);
                }, throwable -> {
                    showVerificationResultDialog(CertificateVerificationResult.INVALID_CERT);
                    Timber.e(throwable, "Error!");

                    ExceptionWithResourceString throwableRS = (ExceptionWithResourceString)throwable;
                    showVerificationFailureDialog(throwableRS.getErrorMessageResId());
                });
    }

    @Override
    protected void displayErrors(Throwable throwable, DialogUtils.ErrorCategory errorCategory, @StringRes int errorTitleResId) {
        hideVerificationProgressDialog();
        super.displayErrors(throwable, errorCategory, errorTitleResId);
    }
}

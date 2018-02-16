package com.learningmachine.android.app.ui.cert;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.CertificateManager;
import com.learningmachine.android.app.data.CertificateVerifier;
import com.learningmachine.android.app.data.IssuerManager;
import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.cert.BlockCertParser;
import com.learningmachine.android.app.data.cert.v20.BlockCertV20;
import com.learningmachine.android.app.data.error.ExceptionWithResourceString;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.data.model.CertificateRecord;
import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.data.model.TxRecord;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;
import com.learningmachine.android.app.databinding.FragmentCertificateBinding;
import com.learningmachine.android.app.dialog.AlertDialogFragment;
import com.learningmachine.android.app.ui.LMFragment;
import com.learningmachine.android.app.ui.onboarding.OnboardingActivity;
import com.learningmachine.android.app.util.DialogUtils;
import com.learningmachine.android.app.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import timber.log.Timber;


public class CertificateFragment extends LMFragment {

    private static final String ARG_CERTIFICATE_UUID = "CertificateFragment.CertificateUuid";
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

        mBinding.verifyButton.setOnClickListener(view -> verifyCertificate() );
        mBinding.shareButton.setOnClickListener(view -> shareCertificate() );

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
            case R.id.fragment_certificate_info_menu_item:
                viewCertificateInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupWebView() {

        // Note: This entire code has been reworked to more closely match the iOS application.
        WebSettings webSettings = mBinding.webView.getSettings();
        webSettings.setJavaScriptEnabled(false);
        webSettings.setAllowFileAccessFromFileURLs(false);
        mBinding.webView.setWebViewClient(new LMWebViewClient());

        mBinding.progressBar.setVisibility(View.VISIBLE);
        mBinding.progressBar.animate();

        mCertificateVerifier.loadCertificate(mCertUuid)
                .compose(bindToMainThread())
                .subscribe(certificate -> {
                    // Display the actual certificate
                    String displayHTML = "";

                    if(certificate instanceof BlockCertV20) {
                        BlockCertV20 cert2 = (BlockCertV20) certificate;
                        displayHTML = cert2.getDisplayHtml();
                    }else{
                        displayHTML = "BlockCerts Wallet only supports certificates which match the v2.0 specification.";
                    }

                    String normalizeCss = "/*! normalize.css v7.0.0 | MIT License | github.com/necolas/normalize.css */html{line-height:1.15;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100%}body{margin:0}article,aside,footer,header,nav,section{display:block}h1{font-size:2em;margin:.67em 0}figcaption,figure,main{display:block}figure{margin:1em 40px}hr{box-sizing:content-box;height:0;overflow:visible}pre{font-family:monospace,monospace;font-size:1em}a{background-color:transparent;-webkit-text-decoration-skip:objects}abbr[title]{border-bottom:none;text-decoration:underline;text-decoration:underline dotted}b,strong{font-weight:inherit}b,strong{font-weight:bolder}code,kbd,samp{font-family:monospace,monospace;font-size:1em}dfn{font-style:italic}mark{background-color:#ff0;color:#000}small{font-size:80%}sub,sup{font-size:75%;line-height:0;position:relative;vertical-align:baseline}sub{bottom:-.25em}sup{top:-.5em}audio,video{display:inline-block}audio:not([controls]){display:none;height:0}img{border-style:none}svg:not(:root){overflow:hidden}button,input,optgroup,select,textarea{font-family:sans-serif;font-size:100%;line-height:1.15;margin:0}button,input{overflow:visible}button,select{text-transform:none}[type=reset],[type=submit],button,html [type=button]{-webkit-appearance:button}[type=button]::-moz-focus-inner,[type=reset]::-moz-focus-inner,[type=submit]::-moz-focus-inner,button::-moz-focus-inner{border-style:none;padding:0}[type=button]:-moz-focusring,[type=reset]:-moz-focusring,[type=submit]:-moz-focusring,button:-moz-focusring{outline:1px dotted ButtonText}fieldset{padding:.35em .75em .625em}legend{box-sizing:border-box;color:inherit;display:table;max-width:100%;padding:0;white-space:normal}progress{display:inline-block;vertical-align:baseline}textarea{overflow:auto}[type=checkbox],[type=radio]{box-sizing:border-box;padding:0}[type=number]::-webkit-inner-spin-button,[type=number]::-webkit-outer-spin-button{height:auto}[type=search]{-webkit-appearance:textfield;outline-offset:-2px}[type=search]::-webkit-search-cancel-button,[type=search]::-webkit-search-decoration{-webkit-appearance:none}::-webkit-file-upload-button{-webkit-appearance:button;font:inherit}details,menu{display:block}summary{display:list-item}canvas{display:inline-block}template{display:none}[hidden]{display:none}/*# sourceMappingURL=normalize.min.css.map */";
                    String customCss = "body {padding: 20px; font-size: 12px; line-height: 1.5;} body > section { padding: 0;} body section { max-width: 100%; } body img { max-width: 100%; height: auto; width: inherit; }";
                    String wrappedHtml = String.format("<!doctype html><html class=\"no-js\" lang=\"\"><head><meta charset=\"utf-8\"><meta http-equiv=\"x-ua-compatible\" content=\"ie=edge\"><title></title><meta content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0\" name=\"viewport\" /><meta name=”viewport” content=”width=device-width” /><style type=\"text/css\">%s</style><style type=\"text/css\">%s</style></head><body>%s</body></html>", normalizeCss, customCss, displayHTML);
                    mBinding.webView.loadData(wrappedHtml, "text/html; charset=UTF-8", null);

                }, throwable -> {
                    Timber.e(throwable, "Error!");

                    ExceptionWithResourceString throwableRS = (ExceptionWithResourceString)throwable;
                    showVerificationFailureDialog(throwableRS.getErrorMessageResId());
                });
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
            mBinding.progressBar.clearAnimation();
            mBinding.progressBar.setVisibility(View.GONE);
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

        DialogUtils.showAlertDialog(getContext(), this,
                0,
                getResources().getString(R.string.fragment_certificate_share_title),
                getResources().getString(R.string.fragment_certificate_share_message),
                getResources().getString(R.string.fragment_certificate_share_url_button_title),
                getResources().getString(R.string.fragment_certificate_share_file_button_title),
                (btnIdx) -> {
                    if((int)btnIdx == 0) {
                        shareCertificateTypeResult(true);
                    }
                    if((int)btnIdx == 1) {
                        shareCertificateTypeResult(false);
                    }
                    return null;
                });

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





    private TextView updateDialogTitleView = null;
    private TextView updateDialogMessageView = null;
    private AlertDialogFragment updateDialog = null;

    private void showVerficationProgressDialog() {

        if(updateDialog == null) {
            mCancelVerification = false;
            updateDialog = DialogUtils.showCustomDialog(getContext(), this,
                    R.layout.dialog_certificate_verification,
                    0,
                    "",
                    "",
                    null,
                    getResources().getString(R.string.onboarding_passphrase_cancel),
                    (btnIdx) -> {
                        mCancelVerification = true;
                        updateDialog = null;
                        updateDialogTitleView = null;
                        updateDialogMessageView = null;
                        return null;
                    },
                    (dialogContent) -> {
                        View view = (View) dialogContent;
                        updateDialogTitleView = (TextView) view.findViewById(R.id.titleView);
                        updateDialogMessageView = (TextView) view.findViewById(R.id.messageView);

                        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
                        progressBar.animate();

                        this.updateVerficationProgressDialog(0, R.string.cert_verification_step0);
                        return null;
                    });
        }
    }

    private void updateVerficationProgressDialog(int stepNumber, int messageResId) {
        if(updateDialogTitleView != null && updateDialogMessageView != null) {
            String title = getString(R.string.fragment_verify_cert_step_format, stepNumber, 6);
            String message = getString(messageResId);
            updateDialogTitleView.setText(title);
            updateDialogMessageView.setText(message);
        }
    }

    private void hideVerificationProgressDialog() {
        if(updateDialog != null) {
            updateDialog.dismissAllowingStateLoss();
        }
        updateDialog = null;
        updateDialogTitleView = null;
        updateDialogMessageView = null;
    }




    private void showVerificationResultDialog(int iconId, int titleId, int messageId) {
        hideVerificationProgressDialog();

        DialogUtils.showAlertDialog(getContext(), this,
                iconId,
                getResources().getString(titleId),
                getResources().getString(messageId),
                null,
                getResources().getString(R.string.onboarding_passphrase_ok),
                (btnIdx) -> {
                    return null;
                });
    }

    private void showVerificationFailureDialog(int errorId) {
        hideVerificationProgressDialog();

        DialogUtils.showAlertDialog(getContext(), this,
                R.drawable.ic_dialog_failure,
                getResources().getString(R.string.cert_verification_failure_title),
                getResources().getString(errorId),
                null,
                getResources().getString(R.string.onboarding_passphrase_ok),
                (btnIdx) -> {
                    return null;
                });
    }

    private void verifyCertificate() {
        showVerficationProgressDialog();

        // if there is no internet connection, and unhandled exception is thrown.  Let's catch it.
        Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException (Thread thread, Throwable e)
            {
                showVerificationFailureDialog(R.string.error_no_internet);
            }
        });

        updateVerficationProgressDialog(0, R.string.cert_verification_step0);

        mIssuerManager.certificateVerified(mCertUuid)
                .compose(bindToMainThread())
                .subscribe(aVoid -> Timber.d("Issuer analytics: Certificate verified"),
                        throwable -> Timber.e(throwable, "Issuer has no analytics url."));


        // load the certificate
        mCertificateVerifier.loadCertificate(mCertUuid)
                .compose(bindToMainThread())
                .subscribe(certificate -> {

                    // load the TX record
                    mCertificateVerifier.loadTXRecord(certificate).compose(bindToMainThread())
                            .subscribe(txRecord -> {

                                // begin the verification process
                                step1_CompareComputedHashWithExpectedHash(certificate, txRecord);

                            }, throwable -> {
                                Timber.e(throwable, "Error!");

                                ExceptionWithResourceString throwableRS = (ExceptionWithResourceString)throwable;
                                showVerificationFailureDialog(throwableRS.getErrorMessageResId());
                            });

                }, throwable -> {
                    Timber.e(throwable, "Error!");

                    ExceptionWithResourceString throwableRS = (ExceptionWithResourceString)throwable;
                    showVerificationFailureDialog(throwableRS.getErrorMessageResId());
                });
    }

    private void step1_CompareComputedHashWithExpectedHash(BlockCert certificate, TxRecord txRecord) {
        if (mCancelVerification) {
            return;
        }

        updateVerficationProgressDialog(1, R.string.cert_verification_step1);

        mCertificateVerifier.CompareComputedHashWithExpectedHash(certificate, txRecord)
                .compose(bindToMainThread())
                .subscribe((localHash) -> {
                    step2_EnsuringMerkleReceiptIsValid(certificate, txRecord);
                }, throwable -> {
                    ExceptionWithResourceString throwableRS = (ExceptionWithResourceString)throwable;
                    showVerificationFailureDialog(throwableRS.getErrorMessageResId());
                });
    }

    private void step2_EnsuringMerkleReceiptIsValid(BlockCert certificate, TxRecord txRecord) {
        if (mCancelVerification) {
            return;
        }

        updateVerficationProgressDialog(2, R.string.cert_verification_step2);

        mCertificateVerifier.EnsuringMerkleReceiptIsValid(certificate, txRecord)
                .compose(bindToMainThread())
                .subscribe(nothing -> {
                    step3_ComparingExpectedMerkleRootWithValueOnTheBlockchain(certificate, txRecord);
                }, throwable -> {
                    ExceptionWithResourceString throwableRS = (ExceptionWithResourceString)throwable;
                    showVerificationFailureDialog(throwableRS.getErrorMessageResId());
                });

    }

    private void step3_ComparingExpectedMerkleRootWithValueOnTheBlockchain(BlockCert certificate, TxRecord txRecord) {
        if (mCancelVerification) {
            return;
        }

        updateVerficationProgressDialog(3, R.string.cert_verification_step3);

        mCertificateVerifier.ComparingExpectedMerkleRootWithValueOnTheBlockchain(certificate, txRecord)
                .compose(bindToMainThread())
                .subscribe(nothing -> {
                    step4_ValidatingIssuerIdentity(certificate, txRecord);
                }, throwable -> {
                    ExceptionWithResourceString throwableRS = (ExceptionWithResourceString)throwable;
                    showVerificationFailureDialog(throwableRS.getErrorMessageResId());
                });
    }

    private void step4_ValidatingIssuerIdentity(BlockCert certificate, TxRecord txRecord) {
        if (mCancelVerification) {
            return;
        }

        updateVerficationProgressDialog(4, R.string.cert_verification_step5);

        mCertificateVerifier.ValidatingIssuerIdentity(certificate, txRecord)
                .compose(bindToMainThread())
                .subscribe(issuerResponse -> {
                    step5_CheckingIfTheCredentialHasBeenRevoked(certificate, txRecord, issuerResponse);
                }, throwable -> {
                    ExceptionWithResourceString throwableRS = (ExceptionWithResourceString)throwable;
                    showVerificationFailureDialog(throwableRS.getErrorMessageResId());
                });
    }

    private void step5_CheckingIfTheCredentialHasBeenRevoked(BlockCert certificate, TxRecord txRecord, IssuerResponse issuerResponse) {
        if (mCancelVerification) {
            return;
        }

        updateVerficationProgressDialog(5, R.string.cert_verification_step4);

        mCertificateVerifier.CheckingIfTheCredentialHasBeenRevoked(certificate, txRecord, issuerResponse)
                .compose(bindToMainThread())
                .subscribe(nothing -> {
                    step6_CheckingExpirationDate(certificate, txRecord, issuerResponse);
                }, throwable -> {
                    ExceptionWithResourceString throwableRS = (ExceptionWithResourceString)throwable;
                    showVerificationFailureDialog(throwableRS.getErrorMessageResId());
                });
    }

    private void step6_CheckingExpirationDate(BlockCert certificate, TxRecord txRecord, IssuerResponse issuerResponse) {
        if (mCancelVerification) {
            return;
        }

        updateVerficationProgressDialog(6, R.string.cert_verification_step6);

        mCertificateVerifier.CheckingExpirationDate(certificate, txRecord, issuerResponse)
                .compose(bindToMainThread())
                .subscribe(issuerResponse2 -> {

                    showVerificationResultDialog(R.drawable.ic_dialog_success, R.string.cert_verification_success_title, R.string.cert_verification_step_valid_cert);

                }, throwable -> {
                    Timber.e(throwable, "Error! Merkle roots do not match");

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

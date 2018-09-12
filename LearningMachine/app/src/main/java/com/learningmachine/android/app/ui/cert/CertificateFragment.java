package com.learningmachine.android.app.ui.cert;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
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
import com.learningmachine.android.app.data.cert.v20.BlockCertV20;
import com.learningmachine.android.app.data.error.ExceptionWithResourceString;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.data.model.CertificateRecord;
import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.data.verifier.VerificationSteps;
import com.learningmachine.android.app.data.verifier.VerifierStatus;
import com.learningmachine.android.app.databinding.FragmentCertificateBinding;
import com.learningmachine.android.app.dialog.AlertDialogFragment;
import com.learningmachine.android.app.ui.LMFragment;
import com.learningmachine.android.app.util.DialogUtils;
import com.learningmachine.android.app.util.FileUtils;

import java.io.File;

import javax.inject.Inject;

import rx.Observable;
import timber.log.Timber;

public class CertificateFragment extends LMFragment {

    private static final String ARG_CERTIFICATE_UUID = "CertificateFragment.CertificateUuid";
    private static final String FILE_PROVIDER_AUTHORITY = "com.learningmachine.android.app.fileprovider";
    private static final String TEXT_MIME_TYPE = "text/plain";

    @Inject protected CertificateManager mCertificateManager;
    @Inject protected IssuerManager mIssuerManager;
    @Inject protected CertificateVerifier mCertificateVerifier;

    private FragmentCertificateBinding mBinding;
    private String mCertUuid;
    private AlertDialogFragment mUpdateDialog;
    private boolean mWasCanceled;

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
        setAllItemsChecked();
        mBinding.certBottomNavigation.setOnNavigationItemSelectedListener(item -> {
            setAllItemsChecked();
            switch (item.getItemId()) {
                case R.id.fragment_certificate_info_menu_item:
                    Timber.i("More info tapped on the Certificate display");
                    viewCertificateInfo();
                    return true;
                case R.id.fragment_certificate_verify_menu_item:
                    Timber.i("Verify Certificate tapped on the Certificate display");
                    verifyCertificate();
                    return true;
                case R.id.fragment_certificate_share_menu_item:
                    Timber.i("Share Certificate tapped on the Certificate display");
                    shareCertificate();
                    return true;
            }
            return false;
        });
        setupWebView();

        return mBinding.getRoot();
    }

    /**
     * This method will make all items in menu have the same size.
     */
    private void setAllItemsChecked(){
        Menu menu = mBinding.certBottomNavigation.getMenu();

        for (int i = 0, size = menu.size(); i < size; i++) {
            MenuItem item = menu.getItem(i);
            item.setChecked(false);
            item.setCheckable(false);
        }
    }

    private String prepareForCertificateVerification() {
        try {
            // 0. copy VERIFY_LIB_FILE_PATH to documents folder
            // 1. copy VERIFY_FILE_PATH to documents folder
            // 2. copy the certificate to "certificate.json"
            // 3. return the URL to the HTML file

            FileUtils.copyAssetFile(getContext(), "www/verifier.js", "verifier.js");
            FileUtils.copyAssetFile(getContext(), "www/verify.html", "verify.html");

            String certificateJSON = FileUtils.getCertificateFileJSON(getContext(), mCertUuid);
            String localJsonPath = getContext().getFilesDir() + "/" + "certificate.json";
            FileUtils.writeStringToFile(certificateJSON, localJsonPath);

            return "file://" + getContext().getFilesDir() + "/" + "verify.html";

        } catch (Exception e) {
            Timber.e(e, "Unable to prepare the certificate verification system.");
            return "Unable to prepare the certificate verification system<br>"+e.toString();
        }
    }

    private String displayHTML(BlockCert certificate) {
        String displayHTML = "";

        if(certificate instanceof BlockCertV20) {
            BlockCertV20 cert2 = (BlockCertV20) certificate;
            displayHTML = cert2.getDisplayHtml();

            if(displayHTML == null) {
                displayHTML = "<center>" + getString(R.string.cert_old_version_error) + "</center>";
            }
        }else{
            displayHTML = "<center>" + getString(R.string.cert_old_version_error) + "</center>";
        }

        String normalizeCss = "/*! normalize.css v7.0.0 | MIT License | github.com/necolas/normalize.css */html{line-height:1.15;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100%}body{margin:0}article,aside,footer,header,nav,section{display:block}h1{font-size:2em;margin:.67em 0}figcaption,figure,main{display:block}figure{margin:1em 40px}hr{box-sizing:content-box;height:0;overflow:visible}pre{font-family:monospace,monospace;font-size:1em}a{background-color:transparent;-webkit-text-decoration-skip:objects}abbr[title]{border-bottom:none;text-decoration:underline;text-decoration:underline dotted}b,strong{font-weight:inherit}b,strong{font-weight:bolder}code,kbd,samp{font-family:monospace,monospace;font-size:1em}dfn{font-style:italic}mark{background-color:#ff0;color:#000}small{font-size:80%}sub,sup{font-size:75%;line-height:0;position:relative;vertical-align:baseline}sub{bottom:-.25em}sup{top:-.5em}audio,video{display:inline-block}audio:not([controls]){display:none;height:0}img{border-style:none}svg:not(:root){overflow:hidden}button,input,optgroup,select,textarea{font-family:sans-serif;font-size:100%;line-height:1.15;margin:0}button,input{overflow:visible}button,select{text-transform:none}[type=reset],[type=submit],button,html [type=button]{-webkit-appearance:button}[type=button]::-moz-focus-inner,[type=reset]::-moz-focus-inner,[type=submit]::-moz-focus-inner,button::-moz-focus-inner{border-style:none;padding:0}[type=button]:-moz-focusring,[type=reset]:-moz-focusring,[type=submit]:-moz-focusring,button:-moz-focusring{outline:1px dotted ButtonText}fieldset{padding:.35em .75em .625em}legend{box-sizing:border-box;color:inherit;display:table;max-width:100%;padding:0;white-space:normal}progress{display:inline-block;vertical-align:baseline}textarea{overflow:auto}[type=checkbox],[type=radio]{box-sizing:border-box;padding:0}[type=number]::-webkit-inner-spin-button,[type=number]::-webkit-outer-spin-button{height:auto}[type=search]{-webkit-appearance:textfield;outline-offset:-2px}[type=search]::-webkit-search-cancel-button,[type=search]::-webkit-search-decoration{-webkit-appearance:none}::-webkit-file-upload-button{-webkit-appearance:button;font:inherit}details,menu{display:block}summary{display:list-item}canvas{display:inline-block}template{display:none}[hidden]{display:none}/*# sourceMappingURL=normalize.min.css.map */";
        String customCss = "body {padding: 20px; font-size: 12px; line-height: 1.5;} body > section { padding: 0;} body section { max-width: 100%; } body img { max-width: 100%; height: auto; width: inherit; }";
        String wrappedHtml = String.format("<!doctype html><html class=\"no-js\" lang=\"\"><head><meta charset=\"utf-8\"><meta http-equiv=\"x-ua-compatible\" content=\"ie=edge\"><title></title><meta content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0\" name=\"viewport\" /><meta name=”viewport” content=”width=device-width” /><style type=\"text/css\">%s</style><style type=\"text/css\">%s</style></head><body>%s</body></html>", normalizeCss, customCss, displayHTML);

        return wrappedHtml;
    }

    private void setupWebView() {



        // Note: This entire code has been reworked to more closely match the iOS application.
        mBinding.webView.setWebViewClient(new LMWebViewClient());
        mBinding.progressBar.setVisibility(View.VISIBLE);

        mCertificateVerifier.loadCertificate(mCertUuid)
                .compose(bindToMainThread())
                .subscribe(certificate -> {

                    String html = displayHTML(certificate);
                    mBinding.webView.loadData(html, "text/html; charset=UTF-8", null);

                }, throwable -> {
                    Timber.e(throwable, "Could not setup webview.");

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
        Timber.i("Showing share certificate dialog for " + mCertUuid);
        AlertDialogFragment fragment = DialogUtils.showCustomSheet(getContext(), this,
                R.layout.dialog_share_file_or_url,
                0,
                "",
                "",
                "",
                "",
                (btnIdx) -> {
                    if ((int) btnIdx == 0) {
                        Timber.i("User chose to share certificate via file");
                        shareCertificateTypeResult(true);
                    }
                    if ((int) btnIdx == 1) {
                        Timber.i("User chose to share the certificate via URL");
                        shareCertificateTypeResult(false);
                    }
                    return null;
                },
                (dialogContent) -> null,
                (dialogContent) -> {
                    Timber.i("Share dialog cancelled");
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


    private void showVerificationProgressDialog() {
        mWasCanceled = false;
        mUpdateDialog = DialogUtils.showCustomDialog(getContext(), this,
                0,
                "",
                "",
                null,
                getResources().getString(R.string.onboarding_passphrase_cancel),
                (btnIdx) -> {
                    mWasCanceled = true;
                    return null;
                    },
                (dialogContent) -> {
                    return null;
                },
                (btnIdx) -> {
                    mWasCanceled = true;
                    return null;
                });
    }

    private void updateVerificationProgressDialog(int messageResId) {
       updateVerificationProgressDialog("", "", getString(messageResId));
    }

    private void updateVerificationProgressDialog(String blockChain, String stepLabel, String message) {
        FragmentActivity parent = getActivity();
        if (parent == null || parent.isFinishing()) {
            return;
        }

        parent.runOnUiThread(() -> {
            if (mUpdateDialog == null) {
                return;
            }
            mUpdateDialog.setCustomTitle(blockChain);
            mUpdateDialog.setCustomSubTitle(stepLabel);
            mUpdateDialog.setCustomMessage(message);
        });
    }

    private void hideVerificationProgressDialog() {
        if(mUpdateDialog != null) {
            mUpdateDialog.dismissAllowingStateLoss();
        }
    }

    private void showVerificationSuccessDialog(int iconId, String title, String message) {
        if (mWasCanceled) {
            return;
        }
        hideVerificationProgressDialog();

        DialogUtils.showAlertDialog(getContext(), this,
                iconId,
                title,
                message,
                null,
                getResources().getString(R.string.ok_button),
                (btnIdx) -> {
                    return null;
                });
    }

    private void showVerificationFailureDialog(int errorId) {
        if (mWasCanceled) {
            return;
        }
        hideVerificationProgressDialog();

        DialogUtils.showAlertDialog(getContext(), this,
                R.drawable.ic_dialog_failure,
                getResources().getString(R.string.cert_verification_failure_title),
                getResources().getString(errorId),
                null,
                getResources().getString(R.string.ok_button),
                (btnIdx) -> {
                    return null;
                });
    }

    private void showVerificationFailureDialog(String error, String title) {
        if (mWasCanceled) {
            return;
        }
        hideVerificationProgressDialog();

        DialogUtils.showAlertDialog(getContext(), this,
                R.drawable.ic_dialog_failure,
                title,
                error,
                null,
                getResources().getString(R.string.ok_button),
                (btnIdx) -> null);
    }

    private void verifyCertificate() {
        Timber.i("User tapped verify on this certificate");
        // 0. show the progress dialog
        showVerificationProgressDialog();

        if (!isOnline(getContext())) {
            showVerificationFailureDialog(getString(R.string.error_no_internet_message),
                    getString(R.string.error_no_internet_title));
            return;
        }

        // 1. instrument the verify_view web view to begin javascript verification
        WebSettings webSettings = mBinding.verifyView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        mBinding.verifyView.addJavascriptInterface(new JavascriptInterface(), "Android");

        String urlOrHtml = prepareForCertificateVerification();
        if (urlOrHtml.startsWith("file://")) {
            mBinding.verifyView.loadUrl(urlOrHtml);
        } else {
            // we failed to load the verification lib, error out
            showVerificationFailureDialog(R.string.error_no_engine);
        }

    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    @Override
    protected void displayErrors(Throwable throwable, DialogUtils.ErrorCategory errorCategory, @StringRes int errorTitleResId) {
        hideVerificationProgressDialog();
        super.displayErrors(throwable, errorCategory, errorTitleResId);
    }

    /**
     * This class has methods that will be called by JavaScript code.
     * JavaScript will notify changes in Status when a certificate is being verified.
     */
    private class JavascriptInterface {
        private static final String CHECK_REVOKED_STATUS = "checkRevokedStatus";
        private VerificationSteps[] mVerificationSteps;
        private String mChainName;
        private int mSubStepsTotalCount;
        private int mSubStepsCount;

        /**
         * This method will be called when a new Status is available in a Credential verification process.
         * @param statusStr The status String in JSON format.
         */
        @android.webkit.JavascriptInterface
        public void notifyStatusChanged(String statusStr) {

            VerifierStatus status = VerifierStatus.getFromString(statusStr);
            String stepLabel = getStepLabelFromSubStep(status.code);
            if (status.isSuccess()) {
                String title = getString(R.string.fragment_verify_cert_chain_format, mChainName);
                updateVerificationProgressDialog(title, stepLabel, status.label);

                mSubStepsCount++;
                if (mSubStepsCount == mSubStepsTotalCount) {
                    String successTitle = getString(R.string.cert_verification_success_title);
                    String successMessage = getString(R.string.success_verification, mChainName);
                    showVerificationSuccessDialog(R.drawable.ic_dialog_success,
                            successTitle,
                            successMessage);
                }
            } else if (status.isFailure()) {
                String failureTitle = getResources().getString(R.string.cert_verification_failure_title);
                if (status.code.equals(CHECK_REVOKED_STATUS)) {
                    failureTitle = getResources().getString(R.string.cert_verification_revoked_title);
                }
                showVerificationFailureDialog(status.errorMessage, failureTitle);
            }
        }

        /**
         * This method will receive the Sub Steps.
         * @param verificationStepsStr The substeps String in JSON format.
         */
        @android.webkit.JavascriptInterface
        public void notifyVerificationSteps(String verificationStepsStr) {
            mVerificationSteps = VerificationSteps.getFromString(verificationStepsStr);
            mSubStepsTotalCount = getSubStepsCount(mVerificationSteps);
            mSubStepsCount = 0;
        }

        /**
         * This method will receive the chain type code.
         * @param chainName The chain name. Such as mocknet, bitcoin and testnet.
         */
        @android.webkit.JavascriptInterface
        public void notifyChainName(String chainName) {
            mChainName = chainName;
        }

        private String getStepLabelFromSubStep(String code) {
            for (VerificationSteps step :
                    mVerificationSteps) {
                for (VerificationSteps.SubSteps subStep:
                        step.subSteps) {
                    if (subStep.code.equals(code)) {
                        return step.label;
                    }
                }
            }
            return null;
        }

        private int getSubStepsCount(VerificationSteps[] steps) {
            int count = 0;
            for (VerificationSteps step :
                    steps) {
                count += step.subSteps.length;
            }
            return count;
        }
    }
}

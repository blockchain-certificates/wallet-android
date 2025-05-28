package com.hyland.android.app.ui.cert;

import android.app.Activity;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;

import com.hyland.android.app.R;
import com.hyland.android.app.data.cert.v20.Anchor;
import com.hyland.android.app.data.inject.Injector;
import com.hyland.android.app.data.verifier.VerificationSteps;
import com.hyland.android.app.data.verifier.VerifierStatus;
import com.hyland.android.app.databinding.FragmentVerifyCertificateBinding;
import com.hyland.android.app.util.DialogUtils;
import com.hyland.android.app.util.FileUtils;

import java.lang.ref.WeakReference;

/**
 * A placeholder fragment containing a simple view.
 */
public class VerifyCertificateFragment extends Fragment {
    private static final String ARG_CERTIFICATE_UUID = "VerifyCertificateFragment.CertificateUuid";

    private String mCertUuid;
    private FragmentVerifyCertificateBinding mBinding;
    private boolean mStartedVerification;
    private WeakReference<Activity> mParentActivity;
    private String mChainName;

    public static VerifyCertificateFragment newInstance(String certificateUuid) {

        Bundle args = new Bundle();
        args.putString(ARG_CERTIFICATE_UUID, certificateUuid);

        VerifyCertificateFragment fragment = new VerifyCertificateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mParentActivity = new WeakReference<>(getActivity());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.obtain(mParentActivity.get())
                .inject(this);
        mCertUuid = getArguments().getString(ARG_CERTIFICATE_UUID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_verify_certificate, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mStartedVerification) {
            mStartedVerification = true;
            verifyCertificate();
        }
    }

    private void setupStatus(VerificationSteps[] verificationSteps) {
        if (!isValidFragmentInstance()) {
            return;
        }
        mParentActivity.get().runOnUiThread(() -> {
            showVerificationStartedStatus();
            mBinding.statusView.setOnVerificationFinishListener(withError -> {
                showDoneButton();
                mBinding.statusViewScrollContainer.fullScroll(View.FOCUS_DOWN);
                if (withError) {
                    showVerificationErrorStatus();
                } else {
                    showVerificationSuccessStatus(mChainName);
                }
            });
            mBinding.statusView.addVerificationSteps(verificationSteps);
        });
    }

    private void setupChainName (String chainName) {
        mChainName = chainName;
    }

    private boolean isValidFragmentInstance() {
        return isAdded() && mParentActivity.get() != null && !mParentActivity.get().isFinishing();
    }

    private void showVerificationStartedStatus() {
        String status = getString(R.string.fragment_verify_cert_chain_format);
        mBinding.verificationStatus.setText(status);
    }

    private void showVerificationSuccessStatus(String chainName) {
        String status = getSuccessStatusString(chainName);
        mBinding.verificationStatus.setText(status);
        mBinding.verificationStatus.setTextColor(getResources().getColor(R.color.c3));
        mBinding.verificationStatus.setBackgroundColor(getResources().getColor(R.color.c14));
    }

    private String getSuccessStatusString(String chainName) {
        if (chainName == null) {
            chainName = "";
        }
        String status = getString(R.string.success_verification, chainName);
        status.replace("  ", " "); // replace double spaces if chainName is empty string
        status.replace(" .", ".");
        return status;
    }

    private void showVerificationErrorStatus() {
        String status = getString(R.string.error_verification);
        mBinding.verificationStatus.setText(status);
        mBinding.verificationStatus.setTextColor(getResources().getColor(R.color.c9));
        mBinding.verificationStatus.setBackgroundColor(getResources().getColor(R.color.c15));
    }

    private void showDoneButton() {
        mBinding.doneVerification.setVisibility(View.VISIBLE);
        mBinding.doneVerification.setEnabled(true);
        mBinding.doneVerification.setOnClickListener(v -> mParentActivity.get().finish());
    }

    private void activateSubStep(VerifierStatus status) {
        if (!isValidFragmentInstance()) {
            return;
        }
        mParentActivity.get().runOnUiThread(() -> mBinding.statusView.activateSubStep(status));
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
            return "Unable to prepare the certificate verification system<br>"+e.toString();
        }
    }

    private void showVerificationFailureDialog(int errorId) {
        DialogUtils.showAlertDialog(getContext(), this,
                R.drawable.ic_dialog_failure,
                getResources().getString(R.string.cert_verification_failure_title),
                getResources().getString(errorId),
                null,
                getResources().getString(R.string.ok_button),
                (btnIdx) -> null);
    }

    private void showVerificationFailureDialog(String error, String title) {
        DialogUtils.showAlertDialog(getContext(), this,
                R.drawable.ic_dialog_failure,
                title,
                error,
                null,
                getResources().getString(R.string.ok_button),
                (btnIdx) -> null);
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    private void verifyCertificate() {
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

    /**
     * This class has methods that will be called by JavaScript code.
     * JavaScript will notify changes in Status when a certificate is being verified.
     */
    private class JavascriptInterface {
        private VerificationSteps[] mVerificationSteps;
        private Anchor.ChainType mChainType;

        /**
         * This method will be called when a new Status is available in a Credential verification process.
         * @param statusStr The status String in JSON format.
         */
        @android.webkit.JavascriptInterface
        public void notifyStatusChanged(String statusStr) {
            VerifierStatus status = VerifierStatus.getFromString(statusStr);
            activateSubStep(status);
        }

        /**
         * This method will receive the Sub Steps.
         * @param verificationStepsStr The substeps String in JSON format.
         */
        @android.webkit.JavascriptInterface
        public void notifyVerificationSteps(String verificationStepsStr) {
            mVerificationSteps = VerificationSteps.getFromString(verificationStepsStr);
            setupStatus(mVerificationSteps);
        }

        /**
         * This method will receive the chain type code.
         * @param chainName The chain name. Such as mocknet, bitcoin and testnet.
         */
        @android.webkit.JavascriptInterface
        public void notifyChainName(String chainName) {
            setupChainName(chainName);
        }
    }

}

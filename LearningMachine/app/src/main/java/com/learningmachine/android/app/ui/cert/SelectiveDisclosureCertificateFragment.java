package com.learningmachine.android.app.ui.cert;

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

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.cert.v20.Anchor;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.databinding.FragmentSelectiveDisclosureCertificateBinding;
import com.learningmachine.android.app.util.DialogUtils;
import com.learningmachine.android.app.util.FileUtils;

import java.lang.ref.WeakReference;
import timber.log.Timber;

public class SelectiveDisclosureCertificateFragment extends Fragment {
    private static final String ARG_CERTIFICATE_UUID = "SelectiveDisclosureCertificateFragment.CertificateUuid";

    private String mCertUuid;
    private FragmentSelectiveDisclosureCertificateBinding mBinding;
    private WeakReference<Activity> mParentActivity;
    private String mChainName;

    public static SelectiveDisclosureCertificateFragment newInstance(String certificateUuid) {

        Bundle args = new Bundle();
        args.putString(ARG_CERTIFICATE_UUID, certificateUuid);

        SelectiveDisclosureCertificateFragment fragment = new SelectiveDisclosureCertificateFragment();
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
        setupSelectiveDisclosureCertificate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_selective_disclosure_certificate, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private String setupSelectiveDisclosureCertificate () {
        try {
            String certificateJSON = FileUtils.getCertificateFileJSON(getContext(), mCertUuid);
            Timber.i("loaded certificate", certificateJSON);
            String localJsonPath = getContext().getFilesDir() + "/" + "certificate.json";
            FileUtils.writeStringToFile(certificateJSON, localJsonPath);
            return "";
        } catch (Exception e) {
            return "Unable to prepare the certificate selective disclosure system<br>"+e.toString();
        }
    }

//    private void setupStatus(VerificationSteps[] verificationSteps) {
//        if (!isValidFragmentInstance()) {
//            return;
//        }
//        mParentActivity.get().runOnUiThread(() -> {
//            showVerificationStartedStatus();
//            mBinding.statusView.setOnVerificationFinishListener(withError -> {
//                showDoneButton();
//                mBinding.statusViewScrollContainer.fullScroll(View.FOCUS_DOWN);
//                if (withError) {
//                    showVerificationErrorStatus();
//                } else {
//                    showVerificationSuccessStatus(mChainName);
//                }
//            });
//            mBinding.statusView.addVerificationSteps(verificationSteps);
//        });
//    }
//
//    private boolean isValidFragmentInstance() {
//        return isAdded() && mParentActivity.get() != null && !mParentActivity.get().isFinishing();
//    }
//
//    private void showVerificationStartedStatus() {
//        String status = getString(R.string.fragment_verify_cert_chain_format);
//        mBinding.verificationStatus.setText(status);
//    }
//
//    private void showVerificationSuccessStatus(String chainName) {
//        String status = getSuccessStatusString(chainName);
//        mBinding.verificationStatus.setText(status);
//        mBinding.verificationStatus.setTextColor(getResources().getColor(R.color.c3));
//        mBinding.verificationStatus.setBackgroundColor(getResources().getColor(R.color.c14));
//    }
//
//    private void showVerificationErrorStatus() {
//        String status = getString(R.string.error_verification);
//        mBinding.verificationStatus.setText(status);
//        mBinding.verificationStatus.setTextColor(getResources().getColor(R.color.c9));
//        mBinding.verificationStatus.setBackgroundColor(getResources().getColor(R.color.c15));
//    }
//
//    private void showDoneButton() {
//        mBinding.doneVerification.setVisibility(View.VISIBLE);
//        mBinding.doneVerification.setEnabled(true);
//        mBinding.doneVerification.setOnClickListener(v -> mParentActivity.get().finish());
//    }
//
//    private void showVerificationFailureDialog(String error, String title) {
//        DialogUtils.showAlertDialog(getContext(), this,
//                R.drawable.ic_dialog_failure,
//                title,
//                error,
//                null,
//                getResources().getString(R.string.ok_button),
//                (btnIdx) -> null);
//    }

}

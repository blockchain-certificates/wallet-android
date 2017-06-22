package com.learningmachine.android.app.dialog;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.CertificateVerifier.CertificateVerificationStatus;
import com.learningmachine.android.app.databinding.FragmentCertificateVerificationProgressBinding;

public class CertificateVerficationProgressFragment extends DialogFragment {

    public interface VerficationCancelListener {
        void onVerificationCancelClick();
    }

    private FragmentCertificateVerificationProgressBinding mBinding;
    private VerficationCancelListener mCancelListener;

    public static CertificateVerficationProgressFragment newInstance() {
        return new CertificateVerficationProgressFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_certificate_verification_progress,
                container,
                false);

        mBinding.progressBar.animate();
        updateVerificationStatus(CertificateVerificationStatus.CHECKING_MERKLE);
        mBinding.cancelTextView.setOnClickListener(v -> {
            dismissAllowingStateLoss();
            if (mCancelListener != null) {
                mCancelListener.onVerificationCancelClick();
            }
        });

        return mBinding.getRoot();
    }

    public void setCancelClickListener(VerficationCancelListener listener) {
        mCancelListener = listener;
    }

    public void updateVerificationStatus(CertificateVerificationStatus status) {
        String title = getString(R.string.fragment_verify_cert_step_format,
                status.getStepNumber(),
                CertificateVerificationStatus.getTotalSteps());
        String message = getString(status.getMessageResId());
        mBinding.titleTextView.setText(title);
        mBinding.messageTextView.setText(message);
    }
}

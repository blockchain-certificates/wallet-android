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
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.CompoundButton;
import android.content.res.ColorStateList;
import android.graphics.Color;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.cert.v20.Anchor;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.databinding.FragmentSelectiveDisclosureCertificateBinding;
import com.learningmachine.android.app.util.DialogUtils;
import com.learningmachine.android.app.util.FileUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import timber.log.Timber;

// TODO: move this away from the view
import com.apicatalog.ld.signature.ecdsa.sd;
import com.apicatalog.vc.holder.Holder;

public class SelectiveDisclosureCertificateFragment extends Fragment {
    private static final String ARG_CERTIFICATE_UUID = "SelectiveDisclosureCertificateFragment.CertificateUuid";

    private String mCertUuid;
    private FragmentSelectiveDisclosureCertificateBinding mBinding;
    private WeakReference<Activity> mParentActivity;
    private List<String> mDisclosurePointers = new ArrayList<String>();
    private JsonObject mCertificate;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_selective_disclosure_certificate, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupSelectiveDisclosureCertificate();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private String setupSelectiveDisclosureCertificate () {
        try {
            String certificateJSON = FileUtils.getCertificateFileJSON(getContext(), mCertUuid);
            Timber.i("loaded certificate for selective disclosure: " + certificateJSON);
            JsonObject certificate = new Gson().fromJson(certificateJSON, JsonObject.class);
            mCertificate = certificate;
            JsonObject credentialSubject = certificate.get("credentialSubject").getAsJsonObject();
            displaySelectiveDisclosureData(credentialSubject, "");

            return "";
        } catch (Exception e) {
            Timber.e(e, "Unable to prepare the certificate selective disclosure system");
            return "Unable to prepare the certificate selective disclosure system<br>"+e.toString();
        }
    }

    private void displaySelectiveDisclosureData (JsonObject credentialSubject, String parentLabel) {
        Timber.i("displaySelectiveDisclosureData: " + credentialSubject.toString());
        Set members = credentialSubject.entrySet();
        for (Object member : members) {
            String[] memberSplitStrings = member.toString().split("=");
            String memberLabel = memberSplitStrings[0];
            String jsonPointer = parentLabel != "" ? "/credentialSubject/" + parentLabel + "/" + memberLabel : "/credentialSubject/" + memberLabel;
            Timber.i("member pointer: " + jsonPointer);
            if (memberSplitStrings[1].startsWith("{")) {
                JsonObject memberObject = new Gson().fromJson(memberSplitStrings[1], JsonObject.class);
                displaySelectiveDisclosureData(memberObject, memberLabel);
                return;
            }

            String memberValue = memberSplitStrings[1];

            TextView textView = new TextView(getContext());
            textView.setText(memberLabel);
            mBinding.selectiveDisclosureLayout.addView(textView);

            CheckBox checkbox = new CheckBox(getContext());
            checkbox.setText(memberValue);
            checkbox.setPadding(20, 0, 0, 0);
            checkbox.setButtonTintList(ColorStateList.valueOf(Color.BLACK));

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String msg = "You have " + (isChecked ? "checked" : "unchecked") + " " + jsonPointer;
                    if (mDisclosurePointers.contains(jsonPointer)) {
                        mDisclosurePointers.remove(jsonPointer);
                    } else {
                        mDisclosurePointers.add(jsonPointer);
                        // POC try to derive early
                        final Holder HOLDER = Holder.with(new ECDSASelective2023());
                        JsonObject derived = HOLDER.derive(mCertificate, mDisclosurePointers).compacted();
                        Timber.i("Derived: " + derived.toString());
                    }
                    Timber.i("Current disclosure pointers: " + mDisclosurePointers.toString());
                }
            });

            mBinding.selectiveDisclosureLayout.addView(checkbox);
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

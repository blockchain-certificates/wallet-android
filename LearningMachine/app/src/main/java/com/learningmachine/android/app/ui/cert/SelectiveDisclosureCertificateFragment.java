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
import com.learningmachine.android.app.data.url.loader.StaticContextLoader;
import com.apicatalog.jsonld.loader.DocumentLoader;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.Collection;
import java.util.ArrayList;
import timber.log.Timber;
import java.io.StringReader;

// TODO: move this away from the view
import com.learningmachine.android.app.data.cert.SelectiveDisclosureHolder;
import com.apicatalog.ld.signature.ecdsa.sd.ECDSASelective2023;
import com.apicatalog.ld.signature.SigningError;
import com.apicatalog.ld.DocumentError;
import jakarta.json.Json;
import jakarta.json.JsonReader;
import android.os.Looper;
import java.lang.Runnable;

public class SelectiveDisclosureCertificateFragment extends Fragment {
    private static final String ARG_CERTIFICATE_UUID = "SelectiveDisclosureCertificateFragment.CertificateUuid";

    private String mCertUuid;
    private FragmentSelectiveDisclosureCertificateBinding mBinding;
    private WeakReference<Activity> mParentActivity;
    private Collection<String> mDisclosurePointers = new ArrayList<String>();
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
        showDoneButton();
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
                    }
                    Timber.i("Current disclosure pointers: " + mDisclosurePointers.toString());
                }
            });

            mBinding.selectiveDisclosureLayout.addView(checkbox);
        }
    }

    private void showDoneButton() {
        mBinding.doneSelection.setOnClickListener(v -> {
            Timber.i("Done button tapped");
            if (mDisclosurePointers.size() == 0) {
                DialogUtils.showAlertDialog(getContext(), this,
                        R.drawable.ic_dialog_failure,
                        "No data selected",
                        "Please select at least one data point to disclose",
                        null,
                        getResources().getString(R.string.ok_button),
                        (btnIdx) -> null);
                return;
            }
            Timber.i("Disclosure pointers: " + mDisclosurePointers.toString());

            final SelectiveDisclosureHolder HOLDER = new SelectiveDisclosureHolder(new ECDSASelective2023());
            // convert Gson.JsonObject to Jakarta.JsonObject
            JsonReader reader = Json.createReader(new StringReader(mCertificate.toString()));
            jakarta.json.JsonObject certificateAsJakartaJson = reader.readObject();
            try {
                DocumentLoader defaultLoader = (DocumentLoader) new StaticContextLoader();
                Timber.i("attempting to derive the certificate " + certificateAsJakartaJson.toString());
                Timber.i("with pointers " + mDisclosurePointers.toString());
                jakarta.json.JsonObject derived = HOLDER.deriveWithLoader(certificateAsJakartaJson, mDisclosurePointers, defaultLoader).compacted();
                Timber.i("Derived: " + derived.toString());
                mBinding.doneSelection.setText("Redacted Certificate Generated!");
                new android.os.Handler(Looper.getMainLooper()).postDelayed(
                        new Runnable() {
                            public void run() {
                                mBinding.doneSelection.setText("Done");
                            }
                        },
                        1000);
            } catch (SigningError | DocumentError e) {
                Timber.e(e, "Unable to derive the certificate");
            }
        });
    }

}

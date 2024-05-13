package com.learningmachine.android.app.ui.cert;

import android.app.Activity;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.Nullable;
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
import android.util.Pair;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;
import android.net.Uri;
import android.content.Intent;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.cert.v20.Anchor;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.databinding.FragmentSelectiveDisclosureCertificateBinding;
import com.learningmachine.android.app.util.DialogUtils;
import com.learningmachine.android.app.util.FileUtils;
import com.learningmachine.android.app.data.url.loader.StaticContextLoader;
import com.learningmachine.android.app.data.CertificateManager;
import com.learningmachine.android.app.data.IssuerManager;
import com.learningmachine.android.app.ui.LMFragment;
import com.learningmachine.android.app.data.model.IssuerRecord;

import com.apicatalog.jsonld.loader.DocumentLoader;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.Collection;
import java.util.ArrayList;
import timber.log.Timber;
import java.io.StringReader;
import javax.inject.Inject;
import rx.Observable;

import com.learningmachine.android.app.data.cert.SelectiveDisclosureHolder;
import com.apicatalog.ld.signature.ecdsa.sd.ECDSASelective2023;
import com.apicatalog.ld.signature.SigningError;
import com.apicatalog.ld.DocumentError;
import jakarta.json.Json;
import jakarta.json.JsonReader;
import android.os.Looper;
import java.lang.Runnable;

public class SelectiveDisclosureCertificateFragment extends LMFragment {
    private static final String ARG_CERTIFICATE_UUID = "SelectiveDisclosureCertificateFragment.CertificateUuid";
    private static final String FILE_PROVIDER_AUTHORITY = "com.learningmachine.android.app.fileprovider";

    private String mCertUuid;
    private FragmentSelectiveDisclosureCertificateBinding mBinding;
    private WeakReference<Activity> mParentActivity;
    private Collection<String> mDisclosurePointers = new ArrayList<String>();
    private JsonObject mCertificate;

    @Inject
    protected CertificateManager mCertificateManager;
    @Inject
    protected IssuerManager mIssuerManager;

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
                                mBinding.doneSelection.setText("Share now");
                                shareRedactedCertificate(derived);
                            }
                        },
                        1000);
            } catch (SigningError | DocumentError e) {
                Timber.e(e, "Unable to derive the certificate");
            }
        });
    }

    private void shareRedactedCertificate(jakarta.json.JsonObject redactedCertificate) {
        mIssuerManager.certificateShared(mCertUuid)
                .compose(bindToMainThread())
                .subscribe(aVoid -> Timber.d("Issuer analytics: Certificate shared"),
                        throwable -> Timber.e(throwable, "Issuer has no analytics url."));
        Observable.combineLatest(mCertificateManager.getCertificate(mCertUuid),
                        mIssuerManager.getIssuerForCertificate(mCertUuid),
                        Pair::new)
                .compose(bindToMainThread())
                .subscribe(pair -> {

                    Intent intent = new Intent(Intent.ACTION_SEND);

                    IssuerRecord issuer = pair.second;
                    String issuerName = issuer.getName();
//                    File certFile = FileUtils.getCertificateFile(getContext(), mCertUuid);
                    try {
                        File certFile = File.createTempFile("redacted_certificate", ".json", getContext().getCacheDir());
                        FileUtils.appendStringToFile(redactedCertificate.toString(), certFile);
                        Uri uri = FileProvider.getUriForFile(getContext(), FILE_PROVIDER_AUTHORITY, certFile);
                        String type = getContext().getContentResolver()
                                .getType(uri);
                        intent.setType(type);
                        intent.putExtra(Intent.EXTRA_STREAM, uri);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        String sharingText = getString(R.string.fragment_certificate_share_file_format, issuerName);

                        intent.putExtra(Intent.EXTRA_TEXT, sharingText);
                        startActivity(intent);
                    } catch (IOException e) {
                        Timber.e(e, "Unable to share certificate");
                    }
                }, throwable -> Timber.e(throwable, "Unable to share certificate"));
    }

}

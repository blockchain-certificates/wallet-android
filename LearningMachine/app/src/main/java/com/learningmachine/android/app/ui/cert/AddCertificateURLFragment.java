package com.learningmachine.android.app.ui.cert;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.CertificateManager;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.databinding.FragmentAddCertificateUrlBinding;
import com.learningmachine.android.app.ui.LMFragment;
import com.learningmachine.android.app.util.DialogUtils;
import com.learningmachine.android.app.util.StringUtils;

import javax.inject.Inject;

import timber.log.Timber;


public class AddCertificateURLFragment extends LMFragment {

    private static final String ARG_CERT_URL = "AddCertificateURLFragment.CertUrl";

    @Inject CertificateManager mCertificateManager;

    private FragmentAddCertificateUrlBinding mBinding;

    public static AddCertificateURLFragment newInstance(String certUrlString) {
        Bundle args = new Bundle();
        args.putString(ARG_CERT_URL, certUrlString);

        AddCertificateURLFragment fragment = new AddCertificateURLFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.obtain(getContext())
                .inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_certificate_url, container, false);
        mBinding.certificateEditText.setOnEditorActionListener(mActionListener);


        mBinding.certificateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mBinding.certificateEditText.getText().length() > 0) {
                    mBinding.importButton.setEnabled(true);
                } else {
                    mBinding.importButton.setEnabled(false);
                }
            }
        });

        mBinding.importButton.setEnabled(false);
        mBinding.importButton.setOnClickListener(v -> {
            displayProgressDialog(R.string.fragment_add_certificate_progress_dialog_message);
            checkVersion(updateNeeded -> {
                if (!updateNeeded) {
                    addCertificate();
                } else {
                    hideProgressDialog();
                }
            });
        });

        handleArgs();

        return mBinding.getRoot();
    }

    public void updateArgs(String certificateURL) {
        mBinding.certificateEditText.setText(certificateURL);
    }

    private void handleArgs() {
        Bundle args = getArguments();
        if (args == null) {
            return;
        }

        String certUrlString = args.getString(ARG_CERT_URL);
        if (!StringUtils.isEmpty(certUrlString)) {
            mBinding.certificateEditText.setText(certUrlString);
        }
    }

    private void addCertificate() {
        hideKeyboard();
        String url = mBinding.certificateEditText.getText()
                .toString();
        Timber.i("User attempting to add a certificate from " + url);
        mCertificateManager.addCertificate(url)
                .compose(bindToMainThread())
                .subscribe(uuid -> {
                    Timber.d("Cert downloaded");
                    hideProgressDialog();
                    Intent intent = CertificateActivity.newIntent(getContext(), uuid);
                    startActivity(intent);
                    getActivity().finish();
                }, throwable -> {
                    Timber.e("Failed to load certificate from " + url);
                    displayErrors(throwable, DialogUtils.ErrorCategory.CERTIFICATE, R.string.error_title_message);
                });
    }



    private TextView.OnEditorActionListener mActionListener = (v, actionId, event) -> {
        if (actionId == getResources().getInteger(R.integer.action_done) || actionId == EditorInfo.IME_ACTION_DONE) {
            addCertificate();
            return false;
        }
        return false;
    };

}

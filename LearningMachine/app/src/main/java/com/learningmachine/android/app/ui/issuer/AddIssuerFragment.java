package com.learningmachine.android.app.ui.issuer;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.databinding.FragmentAddIssuerBinding;
import com.learningmachine.android.app.ui.LMIssuerBaseFragment;
import com.learningmachine.android.app.ui.home.HomeActivity;
import com.learningmachine.android.app.util.StringUtils;

import timber.log.Timber;

public class AddIssuerFragment extends LMIssuerBaseFragment {

    private FragmentAddIssuerBinding mBinding;

    public static AddIssuerFragment newInstance(String issuerUrlString, String nonce) {
        Bundle args = new Bundle();
        args.putString(ARG_ISSUER_URL, issuerUrlString);
        args.putString(ARG_ISSUER_NONCE, nonce);

        AddIssuerFragment fragment = new AddIssuerFragment();
        fragment.setArguments(args);

        return fragment;
    }

    private void CheckIfImportButtonShouldBeEnabled() {
        boolean shouldEnableButton = mBinding.addIssuerNonceEditText.getText().length() > 0 &&
                mBinding.addIssuerUrlEditText.getText().length() > 0;
        enableImportButton(shouldEnableButton);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_issuer, container, false);

        handleArgs();

        mBinding.addIssuerUrlEditText.setText(mIntroUrl);
        mBinding.addIssuerNonceEditText.setText(mNounce);

        mBinding.addIssuerNonceEditText.setOnEditorActionListener(mActionListener);

        mBinding.importButton.setOnClickListener(v -> {
            Timber.i("Import issuer tapped");
            hideKeyboard();
            startIssuerIntroduction();
        });

        CheckIfImportButtonShouldBeEnabled();
        mBinding.addIssuerNonceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) { }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNounce = mBinding.addIssuerNonceEditText.getText().toString();
                CheckIfImportButtonShouldBeEnabled();
            }
        });

        mBinding.addIssuerUrlEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) { }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mIntroUrl = mBinding.addIssuerUrlEditText.getText().toString();
                CheckIfImportButtonShouldBeEnabled();
            }
        });

        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_add_issuer, menu);
    }

    public void updateArgs(String issuerUrlString, String issuerNonce) {
        if (!StringUtils.isEmpty(issuerUrlString)) {
            mBinding.addIssuerUrlEditText.setText(issuerUrlString);
            mIntroUrl = issuerUrlString;
        }
        if (!StringUtils.isEmpty(issuerNonce)) {
            mBinding.addIssuerNonceEditText.setText(issuerNonce);
            mNounce = issuerNonce;
        }
    }

    @Override
    protected void addIssuerOnSubscribe() {
        enableImportButton(false);
    }

    @Override
    protected void addIssuerOnCompleted() {
        enableImportButton(true);
    }

    @Override
    protected void addIssuerOnError() {
        enableImportButton(true);
    }

    @Override
    protected void addIssuerOnIssuerAdded(String uuid) {
        didAddIssuer(uuid);
    }

    private void enableImportButton(boolean enable) {
        mBinding.importButton.setEnabled(enable);
    }

    private void didAddIssuer(String uuid) {
        hideProgressDialog();

        // Go back to the issuer's listing
        startActivity(new Intent(getActivity(), HomeActivity.class));
        getActivity().finish();
    }

    private void viewIssuer(String uuid) {
        hideProgressDialog();
        Intent intent = IssuerActivity.newIntent(getContext(), uuid);
        startActivity(intent);
        getActivity().finish();
    }

    private TextView.OnEditorActionListener mActionListener = (v, actionId, event) -> {
        if (actionId == getResources().getInteger(R.integer.action_done) || actionId == EditorInfo.IME_ACTION_DONE) {
            hideKeyboard();
            startIssuerIntroduction();
            return false;
        }
        return false;
    };

}

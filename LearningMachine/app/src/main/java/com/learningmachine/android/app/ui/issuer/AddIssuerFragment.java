package com.learningmachine.android.app.ui.issuer;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.IssuerManager;
import com.learningmachine.android.app.data.bitcoin.BitcoinManager;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.data.webservice.request.IssuerIntroductionRequest;
import com.learningmachine.android.app.databinding.FragmentAddIssuerBinding;
import com.learningmachine.android.app.ui.LMFragment;
import com.learningmachine.android.app.ui.WebAuthActivity;
import com.learningmachine.android.app.ui.home.HomeActivity;
import com.learningmachine.android.app.util.DialogUtils;
import com.learningmachine.android.app.util.StringUtils;

import javax.inject.Inject;

import rx.Observable;

public class AddIssuerFragment extends LMFragment {

    private static final String ARG_ISSUER_URL = "AddIssuerFragment.IssuerUrl";
    private static final String ARG_ISSUER_NONCE = "AddIssuerFragment.IssuerNonce";
    private static final int REQUEST_WEB_AUTH = 1;

    @Inject protected BitcoinManager mBitcoinManager;
    @Inject protected IssuerManager mIssuerManager;

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
        if (mBinding.addIssuerNonceEditText.getText().length() > 0 && mBinding.addIssuerUrlEditText.getText().length() > 0) {
            mBinding.importButton.setAlpha(1.0f);
            mBinding.importButton.setEnabled(true);
        } else {
            mBinding.importButton.setAlpha(0.3f);
            mBinding.importButton.setEnabled(false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_issuer, container, false);

        handleArgs();

        mBinding.addIssuerNonceEditText.setOnEditorActionListener(mActionListener);

        mBinding.importButton.setOnClickListener(v -> {
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
                CheckIfImportButtonShouldBeEnabled();
            }
        });


        return mBinding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.obtain(getContext())
                .inject(this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_add_issuer, menu);
    }

    public void updateArgs(String issuerUrlString, String issuerNonce) {
        if (!StringUtils.isEmpty(issuerUrlString)) {
            mBinding.addIssuerUrlEditText.setText(issuerUrlString);
        }
        if (!StringUtils.isEmpty(issuerNonce)) {
            mBinding.addIssuerNonceEditText.setText(issuerNonce);
        }
    }

    private void handleArgs() {
        Bundle args = getArguments();
        if (args == null) {
            return;
        }
        String issuerUrlString = args.getString(ARG_ISSUER_URL);
        if (!StringUtils.isEmpty(issuerUrlString)) {
            mBinding.addIssuerUrlEditText.setText(issuerUrlString);
        }
        String issuerNonce = args.getString(ARG_ISSUER_NONCE);
        if (!StringUtils.isEmpty(issuerNonce)) {
            mBinding.addIssuerNonceEditText.setText(issuerNonce);
        }
    }

    private void startIssuerIntroduction() {
        hideKeyboard();
        String introUrl = mBinding.addIssuerUrlEditText.getText()
                .toString();
        String nonce = mBinding.addIssuerNonceEditText.getText()
                .toString();

        Observable.combineLatest(
                mBitcoinManager.getFreshBitcoinAddress(),
                Observable.just(nonce),
                mIssuerManager.fetchIssuer(introUrl),
                IssuerIntroductionRequest::new)
                .doOnSubscribe(() -> displayProgressDialog(R.string.fragment_add_issuer_adding_issuer_progress_dialog_message))
                .compose(bindToMainThread())
                .subscribe(request -> {
                    if (request.getIssuerResponse().usesWebAuth()) {
                        performWebAuth(request);
                    } else {
                        performStandardIssuerIntroduction(request);
                    }
                }, throwable -> displayErrors(throwable, DialogUtils.ErrorCategory.ISSUER, R.string.error_title_message1));
    }

    private void performStandardIssuerIntroduction(IssuerIntroductionRequest request) {
        mIssuerManager.addIssuer(request)
                .compose(bindToMainThread())
                .subscribe(uuid -> didAddIssuer(uuid), throwable -> displayErrors(throwable, DialogUtils.ErrorCategory.ISSUER, R.string.error_title_message2));
    }

    private void performWebAuth(IssuerIntroductionRequest request) {
        hideProgressDialog();
        Intent intent = WebAuthActivity.newIntent(getContext(), request);
        startActivityForResult(intent, REQUEST_WEB_AUTH);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_WEB_AUTH) {
            if (!WebAuthActivity.isWebAuthSuccess(data) || resultCode != Activity.RESULT_OK) {
                return;
            }
            String bitcoinAddress = WebAuthActivity.getWebAuthBitcoinAddress(data);
            String introUrl = mBinding.addIssuerUrlEditText.getText().toString();
            mIssuerManager.fetchIssuer(introUrl)
                    .doOnSubscribe(() -> displayProgressDialog(R.string.fragment_add_issuer_adding_issuer_progress_dialog_message))
                    .compose(bindToMainThread())
                    .map(issuer -> mIssuerManager.saveIssuer(issuer, bitcoinAddress))
                    .subscribe(this::didAddIssuer, throwable -> displayErrors(throwable, DialogUtils.ErrorCategory.ISSUER, R.string.error_title_message3));
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
            startIssuerIntroduction();
            return false;
        }
        return false;
    };

}

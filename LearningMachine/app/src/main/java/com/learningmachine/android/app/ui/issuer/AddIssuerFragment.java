package com.learningmachine.android.app.ui.issuer;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_issuer, container, false);

        handleArgs();

        mBinding.addIssuerNonceEditText.setOnEditorActionListener(mActionListener);

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
                mBitcoinManager.getBitcoinAddress(),
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
                }, throwable -> displayErrors(throwable, R.string.error_title_message));
    }

    private void performStandardIssuerIntroduction(IssuerIntroductionRequest request) {
        mIssuerManager.addIssuer(request)
                .compose(bindToMainThread())
                .subscribe(uuid -> viewIssuer(uuid), throwable -> displayErrors(throwable, R.string.error_title_message));
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
            String introUrl = mBinding.addIssuerUrlEditText.getText().toString();
            mIssuerManager.fetchIssuer(introUrl)
                    .doOnSubscribe(() -> displayProgressDialog(R.string.fragment_add_issuer_adding_issuer_progress_dialog_message))
                    .compose(bindToMainThread())
                    .map(issuer -> mIssuerManager.saveIssuer(issuer))
                    .subscribe(uuid -> {
                        viewIssuer(uuid);
                    }, throwable -> displayErrors(throwable, R.string.error_title_message));
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fragment_add_issuer_verify:
                startIssuerIntroduction();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

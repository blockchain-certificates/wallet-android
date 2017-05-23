package com.learningmachine.android.app.ui.cert;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
        mBinding.certificateUrlEditText.setOnEditorActionListener(mActionListener);

        handleArgs();

        return mBinding.getRoot();
    }

    private void handleArgs() {
        Bundle args = getArguments();
        if (args == null) {
            return;
        }

        String certUrlString = args.getString(ARG_CERT_URL);
        if (!StringUtils.isEmpty(certUrlString)) {
            mBinding.certificateUrlEditText.setText(certUrlString);
        }
    }

    private void addCertificate() {
        hideKeyboard();
        String url = mBinding.certificateUrlEditText.getText()
                .toString();
        mCertificateManager.addCertificate(url)
                .doOnSubscribe(() -> displayProgressDialog(R.string.fragment_add_certificate_progress_dialog_message))
                .compose(bindToMainThread())
                .subscribe(uuid -> {
                    Timber.d("Cert downloaded");
                    hideProgressDialog();
                    Intent intent = CertificateActivity.newIntent(getContext(), uuid);
                    startActivity(intent);
                    getActivity().finish();
                }, throwable -> displayErrors(throwable, R.string.error_title_message));
    }

    private TextView.OnEditorActionListener mActionListener = (v, actionId, event) -> {
        if (actionId == getResources().getInteger(R.integer.action_done) || actionId == EditorInfo.IME_ACTION_DONE) {
            addCertificate();
            return false;
        }
        return false;
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fragment_add_certificate_verify:
                addCertificate();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

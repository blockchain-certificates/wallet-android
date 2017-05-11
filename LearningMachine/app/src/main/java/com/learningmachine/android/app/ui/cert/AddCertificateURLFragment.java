package com.learningmachine.android.app.ui.cert;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.CertificateManager;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.databinding.FragmentAddCertificateUrlBinding;
import com.learningmachine.android.app.ui.LMFragment;

import javax.inject.Inject;

import timber.log.Timber;


public class AddCertificateURLFragment extends LMFragment {

    @Inject CertificateManager mCertificateManager;

    private FragmentAddCertificateUrlBinding mBinding;

    public static AddCertificateURLFragment newInstance() {
        return new AddCertificateURLFragment();
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

        // TODO remove test code
        String url = "https://certificates.learningmachine.com/certificate/8dc46898e94e4596a311b0faaa42e4a3";
        mBinding.certificateUrlEditText.setText(url);

        return mBinding.getRoot();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fragment_add_certificate_verify:
                String url = mBinding.certificateUrlEditText.getText()
                        .toString();
                mCertificateManager.addCertificate(url)
                        .doOnSubscribe(() -> displayProgressDialog(R.string.fragment_add_certificate_progress_dialog_message))
                        .compose(bindToMainThread())
                        .subscribe(responseBody -> {
                            Timber.d("Cert downloaded");
                            hideProgressDialog();
                            getActivity().finish();
                        }, throwable -> displayErrors(throwable, R.string.error_title_message));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.learningmachine.android.app.ui.onboarding;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.bitcoin.BitcoinManager;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.databinding.FragmentPastePassphraseBinding;
import com.learningmachine.android.app.ui.home.HomeActivity;
import com.learningmachine.android.app.util.StringUtils;

import javax.inject.Inject;

public class PastePassphraseFragment extends OnboardingFragment {

    @Inject protected BitcoinManager mBitcoinManager;

    private FragmentPastePassphraseBinding mBinding;

    public static PastePassphraseFragment newInstance() {
        return new PastePassphraseFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.obtain(getContext())
                .inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_paste_passphrase, container, false);

        mBinding.pastePassphraseEditText.addTextChangedListener(new PastePassphraseTextWatcher());
        mBinding.doneButton.setEnabled(false);
        mBinding.doneButton.setOnClickListener(view -> onDone());

        return mBinding.getRoot();
    }

    private void onDone() {
        String passphrase = mBinding.pastePassphraseEditText.getText()
                .toString();
        mBitcoinManager.setPassphrase(passphrase)
                .compose(bindToMainThread())
                .subscribe(wallet -> {
                    startActivity(new Intent(getActivity(), HomeActivity.class));
                    getActivity().finish();
                }, e -> displayErrors(e, R.string.error_title_message));
    }

    private class PastePassphraseTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            String passphrase = mBinding.pastePassphraseEditText.getText()
                    .toString();
            boolean emptyPassphrase = StringUtils.isEmpty(passphrase);
            mBinding.doneButton.setEnabled(!emptyPassphrase);
        }
    }
}

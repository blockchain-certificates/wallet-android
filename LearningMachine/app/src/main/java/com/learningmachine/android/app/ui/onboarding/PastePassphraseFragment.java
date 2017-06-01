package com.learningmachine.android.app.ui.onboarding;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.bitcoin.BitcoinManager;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.databinding.FragmentPastePassphraseBinding;
import com.learningmachine.android.app.ui.home.HomeActivity;

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

        mBinding.accept.setOnClickListener(view -> onDone());

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
                });
    }
}

package com.learningmachine.android.app.ui.settings.passphrase;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.bitcoin.BitcoinManager;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.databinding.FragmentRevealPassphraseBinding;
import com.learningmachine.android.app.ui.LMFragment;

import javax.inject.Inject;

public class RevealPassphraseFragment extends LMFragment {

    @Inject BitcoinManager mBitcoinManager;
    private FragmentRevealPassphraseBinding mBinding;

    public static Fragment newInstance() {
        return new RevealPassphraseFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.obtain(getContext())
                .inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_reveal_passphrase,
                container,
                false);

        mBitcoinManager.getPassphrase()
                .compose(bindToMainThread())
                .subscribe(this::configureCurrentPassphraseTextView);

        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    private void configureCurrentPassphraseTextView(String currentPassphrase) {
        if (mBinding == null) {
            return;
        }
        mBinding.currentPassphraseTextView.setText(currentPassphrase);
        mBinding.currentPassphraseTextView.setOnLongClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("text", currentPassphrase);
            clipboardManager.setPrimaryClip(clipData);
            showSnackbar(mBinding.getRoot(), R.string.reveal_passphrase_text_copied);
            return true;
        });
    }
}

package com.learningmachine.android.app.ui.onboarding;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.bitcoin.BitcoinManager;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.databinding.FragmentViewPassphraseBinding;
import com.learningmachine.android.app.ui.home.HomeActivity;

import javax.inject.Inject;

public class ViewPassphraseFragment extends OnboardingFragment {

    @Inject protected BitcoinManager mBitcoinManager;

    private FragmentViewPassphraseBinding mBinding;

    public static ViewPassphraseFragment newInstance() {
        return new ViewPassphraseFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.obtain(getContext())
                .inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_passphrase, container, false);

        mBinding.onboardingDoneButton.setOnClickListener(view -> onDone());
        mBinding.onboardingDoneButton.setEnabled(false);

        return mBinding.getRoot();
    }

    @Override
    public void onUserVisible() {
        super.onUserVisible();

        mBitcoinManager.getPassphrase().subscribe(passphrase -> {
            configurePassphraseTextView(passphrase);
            mBinding.onboardingDoneButton.setEnabled(true);
        });

    }

    private void configurePassphraseTextView(String passphrase) {
        if (mBinding == null) {
            return;
        }
        mBinding.onboardingPassphraseTextView.setText(passphrase);
        mBinding.onboardingPassphraseTextView.setOnLongClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("text", passphrase);
            clipboardManager.setPrimaryClip(clipData);
            showSnackbar(mBinding.getRoot(), R.string.reveal_passphrase_text_copied);
            return true;
        });
    }

    @Override
    public boolean isBackAllowed() {
        return false;
    }

    private void onDone() {
        startActivity(new Intent(getActivity(), HomeActivity.class));
        getActivity().finish();
    }
}

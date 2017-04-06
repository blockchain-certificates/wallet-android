package com.learningmachine.android.app.ui.settings.passphrase;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

    public static RevealPassphraseFragment newInstance() {
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
        FragmentRevealPassphraseBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_reveal_passphrase,
                container,
                false);

        String currentPassphrase = mBitcoinManager.getPassphrase();
        binding.currentPassphraseTextview.setText(currentPassphrase);

        return binding.getRoot();
    }
}

package com.learningmachine.android.app.ui.settings.passphrase;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.bitcoin.BitcoinManager;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.databinding.FragmentReplacePassphraseBinding;
import com.learningmachine.android.app.ui.LMFragment;

import javax.inject.Inject;

public class ReplacePassphraseFragment extends LMFragment {

    @Inject protected BitcoinManager mBitcoinManager;

    private FragmentReplacePassphraseBinding mBinding;

    public static Fragment newInstance() {
        return new ReplacePassphraseFragment();
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_replace_passphrase, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_replace_passphrase, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.fragment_replace_passphrase_done_menu_item:
                replacePassphrase();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void replacePassphrase() {
        String passphrase = mBinding.replacePassphraseEditText.getText()
                .toString();
        mBitcoinManager.setPassphrase(passphrase)
                .compose(bindToMainThread())
                .subscribe(wallet -> passphraseUpdated());
    }

    private void passphraseUpdated() {
        showSnackbar(mBinding.getRoot(), R.string.replace_passphrase_success_message);
    }
}

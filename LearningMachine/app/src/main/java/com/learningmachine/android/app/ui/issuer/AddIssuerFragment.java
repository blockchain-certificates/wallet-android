package com.learningmachine.android.app.ui.issuer;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.bitcoin.BitcoinManager;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.data.webservice.IssuerIntroduction;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;
import com.learningmachine.android.app.databinding.FragmentAddIssuerBinding;
import com.learningmachine.android.app.ui.LMFragment;

import javax.inject.Inject;

import timber.log.Timber;

public class AddIssuerFragment extends LMFragment {

    private FragmentAddIssuerBinding mBinding;

    @Inject protected IssuerIntroduction mIssuerIntroduction;
    @Inject protected BitcoinManager mBitcoinManager;

    public static AddIssuerFragment newInstance() {
        return new AddIssuerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_issuer, container, false);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.fragment_add_issuer_verify:
                String introUrl = mBinding.addIssuerUrlEditText.getText()
                        .toString();
                String nonce = mBinding.addIssuerIdentityEditText.getText()
                        .toString();
                String bitcoinAddress = mBitcoinManager.getBitcoinAddress();

                // TODO: retrieve the next public bitcoin address
                mIssuerIntroduction.addIssuer(introUrl, bitcoinAddress, nonce)
                        .compose(bindToMainThread())
                        .subscribe(this::issuerAdded, throwable -> Timber.e(throwable, "Failed to add issuer"));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void issuerAdded(IssuerResponse issuerResponse) {
        // TODO: persist issuer
        // TODO: display success - go back to issuers list
    }
}

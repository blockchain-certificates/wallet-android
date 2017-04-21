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
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.data.webservice.IssuerIntroduction;
import com.learningmachine.android.app.databinding.FragmentAddIssuerBinding;
import com.learningmachine.android.app.ui.LMFragment;

import javax.inject.Inject;

public class AddIssuerFragment extends LMFragment {

    private FragmentAddIssuerBinding mBinding;

    @Inject protected IssuerIntroduction mIssuerIntroduction;

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
                String nonce = mBinding.addIssuerIdentityEditText.getText().toString();

                mIssuerIntroduction.addIssuer(introUrl, nonce).subscribe();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

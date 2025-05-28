package com.hyland.android.app.ui.issuer;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyland.android.app.R;
import com.hyland.android.app.data.IssuerManager;
import com.hyland.android.app.data.bitcoin.BitcoinManager;
import com.hyland.android.app.data.inject.Injector;
import com.hyland.android.app.databinding.FragmentIssuerInfoBinding;
import com.hyland.android.app.ui.LMFragment;

import javax.inject.Inject;

public class IssuerInfoFragment extends LMFragment {

    private static final String ARG_ISSUER_UUID = "IssuerInfoFragment.IssuerUuid";

    @Inject protected IssuerManager mIssuerManager;
    @Inject protected BitcoinManager mBitcoinManager;

    private FragmentIssuerInfoBinding mBinding;

    public static IssuerInfoFragment newInstance(String issuerUuid) {
        Bundle args = new Bundle();
        args.putString(ARG_ISSUER_UUID, issuerUuid);

        IssuerInfoFragment fragment = new IssuerInfoFragment();
        fragment.setArguments(args);

        return fragment;
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_issuer_info, container, false);

        String issuerUuid = getArguments().getString(ARG_ISSUER_UUID);
        // TODO: should find the bitcoin address that was sent to the issuer
        mIssuerManager.getIssuer(issuerUuid)
                .compose(bindToMainThread())
                .subscribe(issuer -> {
                    IssuerInfoViewModel viewModel = new IssuerInfoViewModel(issuer);
                    mBinding.setIssuerInfo(viewModel);
                });

        return mBinding.getRoot();
    }
}

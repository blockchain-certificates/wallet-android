package com.learningmachine.android.app.ui.issuer;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.model.Issuer;
import com.learningmachine.android.app.databinding.FragmentIssuerInfoBinding;
import com.learningmachine.android.app.ui.LMFragment;


public class IssuerInfoFragment extends LMFragment {

    private static final String ARG_ISSUER_INFO = "IssuerInfoFragment.Info";

    private FragmentIssuerInfoBinding mInfoBinding;
    private Issuer mIssuer;

    public static IssuerInfoFragment newInstance(Issuer issuer) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_ISSUER_INFO, issuer);

        IssuerInfoFragment fragment = new IssuerInfoFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIssuer = (Issuer) getArguments().getSerializable(ARG_ISSUER_INFO);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mInfoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_issuer_info, container, false);

        String introducedDate = "Temp";
        String sharedAddress = "mitm";
        IssuerInfoViewModel viewModel = new IssuerInfoViewModel(mIssuer, introducedDate, sharedAddress);
        mInfoBinding.setIssuerInfo(viewModel);
        return mInfoBinding.getRoot();
    }

}

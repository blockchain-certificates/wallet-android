package com.learningmachine.android.app.ui.issuer;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.model.IssuerInfo;
import com.learningmachine.android.app.databinding.FragmentIssuerInfoBinding;
import com.learningmachine.android.app.ui.LMFragment;


public class IssuerInfoFragment extends LMFragment {

    private static final String ARG_ISSUER_INFO = "IssuerInfoFragment.Info";

    private FragmentIssuerInfoBinding mInfoBinding;
    private IssuerInfo mIssuerInfo;

    public static IssuerInfoFragment newInstance(IssuerInfo issuerInfo) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_ISSUER_INFO, issuerInfo);

        IssuerInfoFragment infoFragment = new IssuerInfoFragment();
        infoFragment.setArguments(args);

        return infoFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIssuerInfo = (IssuerInfo) getArguments().getSerializable(ARG_ISSUER_INFO);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mInfoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_issuer_info, container, false);

        IssuerInfo info = new IssuerInfo("April, 4th, 2017", "rekbrgregbr", "google.com", "rashad@bignerdranch.com", "sample");
        mInfoBinding.setViewModel(info);
        return mInfoBinding.getRoot();
    }

}

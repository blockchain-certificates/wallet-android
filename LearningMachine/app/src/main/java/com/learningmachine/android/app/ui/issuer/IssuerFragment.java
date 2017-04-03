package com.learningmachine.android.app.ui.issuer;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.model.Issuer;
import com.learningmachine.android.app.databinding.FragmentIssuerBinding;
import com.learningmachine.android.app.ui.LMFragment;

public class IssuerFragment extends LMFragment {

    private static final String ARG_ISSUER = "IssuerFragment.Issuer";

    private Issuer mIssuer;
    private FragmentIssuerBinding mBinding;

    public static IssuerFragment newInstance(Issuer issuer) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_ISSUER, issuer);

        IssuerFragment fragment = new IssuerFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mIssuer = (Issuer) getArguments().getSerializable(ARG_ISSUER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_issuer, container, false);

        setupRecyclerView();

        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_issuer, menu);
    }

    private void setupRecyclerView() {
    }
}

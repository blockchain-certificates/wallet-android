package com.learningmachine.android.app.ui.issuer;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.databinding.FragmentIssuerAddBinding;
import com.learningmachine.android.app.ui.LMFragment;

public class IssuerAddFragment extends LMFragment {

    private FragmentIssuerAddBinding mBinding;

    public static IssuerAddFragment newInstance() {
     return new IssuerAddFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_issuer_add, container, false);

        return mBinding.getRoot();
    }
}

package com.learningmachine.android.app.ui.onboarding;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.databinding.FragmentViewPassphraseBinding;
import com.learningmachine.android.app.ui.LMFragment;
import com.learningmachine.android.app.ui.home.HomeActivity;

public class ViewPassphraseFragment extends LMFragment {

    private FragmentViewPassphraseBinding mBinding;

    public static ViewPassphraseFragment newInstance() {
        return new ViewPassphraseFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_passphrase, container, false);

        mBinding.generatePassphrase.setOnClickListener(view -> onDone());

        return mBinding.getRoot();
    }

    private void onDone() {
        startActivity(new Intent(getActivity(), HomeActivity.class));
        getActivity().finish();
    }
}

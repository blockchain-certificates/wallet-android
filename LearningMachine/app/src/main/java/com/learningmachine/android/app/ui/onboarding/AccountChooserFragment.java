package com.learningmachine.android.app.ui.onboarding;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.databinding.FragmentAccountChooserBinding;
import com.learningmachine.android.app.ui.LMFragment;

public class AccountChooserFragment extends LMFragment {

    private Callback mCallback;
    private FragmentAccountChooserBinding mBinding;

    public interface Callback {
        void onNewAccount();
        void onExistingAccount();
    }

    public static AccountChooserFragment newInstance() {
        return new AccountChooserFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (Callback) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_account_chooser, container, false);

        mBinding.newAccount.setOnClickListener(view -> mCallback.onNewAccount());
        mBinding.existingAccount.setOnClickListener(view -> mCallback.onExistingAccount());

        return mBinding.getRoot();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
}

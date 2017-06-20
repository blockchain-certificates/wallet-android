package com.learningmachine.android.app.ui.onboarding;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.databinding.FragmentGeneratePassphraseBinding;

public class GeneratePassphraseFragment extends OnboardingFragment {

    private Callback mCallback;
    private FragmentGeneratePassphraseBinding mBinding;

    public interface Callback {
        void onGeneratePassphraseClick();
    }

    public static GeneratePassphraseFragment newInstance() {
        return new GeneratePassphraseFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (Callback) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_generate_passphrase, container, false);

        mBinding.generatePassphrase.setOnClickListener(view -> mCallback.onGeneratePassphraseClick());

        return mBinding.getRoot();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

}

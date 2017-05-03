package com.learningmachine.android.app.ui.cert;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.databinding.FragmentAddCertificateByUrlBinding;
import com.learningmachine.android.app.ui.LMFragment;


public class AddCertificateURLFragment extends LMFragment {

    private FragmentAddCertificateByUrlBinding mBinding;

    public static AddCertificateURLFragment newInstance() {
        return new AddCertificateURLFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_certificate_url, container, false);

        return mBinding.getRoot();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fragment_add_certificate_verify:
                String url = mBinding.certificateAddByUrlEditText.getText().toString();

                //TODO: Start verification process for the URL
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.learningmachine.android.app.ui.cert;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.databinding.FragmentAddCertificateByFileBinding;
import com.learningmachine.android.app.ui.LMFragment;


public class AddCertificateFileFragment extends LMFragment {

    private FragmentAddCertificateByFileBinding mBinding;

    public static AddCertificateFileFragment newInstance() {
        return new AddCertificateFileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_certificate_file, container, false);

        return mBinding.getRoot();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fragment_add_certificate_verify:
                String file = mBinding.certificateAddByFileEditText.getText().toString();

                //TODO: Start verification process for the FILE
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

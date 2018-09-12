package com.learningmachine.android.app.ui.cert;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.CertificateManager;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.databinding.FragmentAddCertificateFileBinding;
import com.learningmachine.android.app.ui.LMFragment;
import com.learningmachine.android.app.util.DialogUtils;

import java.io.File;
import java.io.FileFilter;

import javax.inject.Inject;

import timber.log.Timber;


public class AddCertificateFileFragment extends LMFragment {

    private static final int REQUEST_READ_STORAGE = 124;

    @Inject CertificateManager mCertificateManager;

    private FragmentAddCertificateFileBinding mBinding;
    private File mSelectedFile;

    public static AddCertificateFileFragment newInstance() {
        return new AddCertificateFileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.obtain(getContext())
                .inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_certificate_file, container, false);

        mBinding.chooseFileButton.setOnClickListener(mOnClickListener);

        mBinding.importButton.setOnClickListener(v -> {
            displayProgressDialog(R.string.fragment_add_certificate_progress_dialog_message);
            checkVersion(updateNeeded -> {
                if (!updateNeeded) {
                    addCertificateFile();
                } else {
                    hideProgressDialog();
                }
            });

        });

        mBinding.importButton.setEnabled(false);

        return mBinding.getRoot();
    }


    private View.OnClickListener mOnClickListener = v -> {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_STORAGE);
            Timber.d("Requesting external storage read permission");
            return;
        }

        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (downloadDir.exists()) {
            FileFilter filter = new JsonFilter();
            File[] files = downloadDir.listFiles(filter);
            if (files == null) {
                Timber.e("Unable to list files");
                return;
            }

            // filter to json files, check if 0
            // show snackbar
            showFileDialog(files);
        }
    };

    private void addCertificateFile() {
        if (mSelectedFile == null) {
            return;
        }
        mCertificateManager.addCertificate(mSelectedFile)
                .compose(bindToMainThread())
                .subscribe(uuid -> {
                    Timber.d("Cert copied");
                    hideProgressDialog();
                    Intent intent = CertificateActivity.newIntent(getContext(), uuid);
                    startActivity(intent);
                    getActivity().finish();
                }, throwable -> {
                    Timber.e(throwable, "Importing failed with error");
                    displayErrors(throwable, DialogUtils.ErrorCategory.CERTIFICATE, R.string.error_title_message);
                });
    }

    private void selectFile(File file) {
        if (file == null) {
            Timber.e("Selected file is null");
            return;
        }

        mSelectedFile = file;
        Timber.d("File selected: %1$s", mSelectedFile.getAbsolutePath());

        String filename = mSelectedFile.getName();
        mBinding.chooseFileButton.setText(filename);

        mBinding.importButton.setEnabled(true);
    }

    private void showFileDialog(File[] files) {
        if(files.length == 0) {
            DialogUtils.showAlertDialog(getContext(), this,
                    R.drawable.ic_dialog_failure,
                    getResources().getString(R.string.no_files_downloaded_title),
                    getResources().getString(R.string.no_files_downloaded_message),
                    null,
                    getResources().getString(R.string.ok_button),
                    (btnIdx) -> {
                        return null;
                    });
            return;
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        FileArrayAdapter fileArrayAdapter = new FileArrayAdapter(getContext(), files);
        builder.setAdapter(fileArrayAdapter, (dialog, which) -> {
            File file = fileArrayAdapter.getItem(which);
            selectFile(file);
        });
        builder.show();
    }

    private class JsonFilter implements FileFilter {
        @Override
        public boolean accept(File file) {
            String path = file.getAbsolutePath()
                    .toLowerCase();
            return path.endsWith(".json");
        }
    }

    private class FileArrayAdapter extends ArrayAdapter<File> {

        FileArrayAdapter(Context context, File[] files) {
            super(context, R.layout.dialog_file_chooser, files);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            TextView textView = (TextView) super.getView(position, convertView, parent);

            File file = getItem(position);
            if (file != null) {
                String filename = file.getName();
                textView.setText(filename);
            }

            return textView;
        }
    }
}

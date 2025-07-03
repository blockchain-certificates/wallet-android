package com.learningmachine.android.app.ui.cert;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.databinding.DataBindingUtil;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;

import android.provider.MediaStore;
import android.provider.OpenableColumns;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.inject.Inject;

import rx.Subscription;
import timber.log.Timber;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;


public class AddCertificateFileFragment extends LMFragment {
    private static final int REQUEST_READ_STORAGE = 124;
    private static final int REQUEST_SELECT_FILE = 125;

    @Inject CertificateManager mCertificateManager;

    private FragmentAddCertificateFileBinding mBinding;
    private Uri mSelectedFileUri;
    private Subscription mAddCertificateSubscription;

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

    @Override
    public void onDestroy() {
        if (mAddCertificateSubscription != null) {
            mAddCertificateSubscription.unsubscribe();
        }
        super.onDestroy();
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


    private final View.OnClickListener mOnClickListener = v -> {
        String readPermission = Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(getContext(),
                readPermission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{readPermission},
                    REQUEST_READ_STORAGE);
            Timber.d("Requesting external storage read permission");
            return;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            Intent openJsonCertificateIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            openJsonCertificateIntent.addCategory(Intent.CATEGORY_OPENABLE);
            openJsonCertificateIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
            openJsonCertificateIntent.setType("application/json");
            startActivityForResult(openJsonCertificateIntent, REQUEST_SELECT_FILE);
        } else {
            File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (downloadDir.exists()) {
                FileFilter filter = new JsonFilter();
                File[] files = downloadDir.listFiles(filter);
                if (files == null) {
                    Timber.e("Unable to list files, no JSON files found");
                    return;
                }

                // filter to json files, check if 0
                // show snackbar
                showFileDialog(files);
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_SELECT_FILE && resultCode == Activity.RESULT_OK && data != null) {
            Uri certificateUri = data.getData();
            selectFile(certificateUri);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void addCertificateFile() {
        Timber.i("Adding certificate file");
        if (mSelectedFileUri == null) {
            Timber.e("No file selected");
            return;
        }
        ContentResolver resolver = getContext().getContentResolver();
        try (InputStream certificateInputStream = resolver.openInputStream(mSelectedFileUri)) {
            mAddCertificateSubscription = mCertificateManager.addCertificate(certificateInputStream)
                    .compose(bindToMainThread())
                    .subscribe(uuid -> {
                        Timber.d("Certificate copied");
                        hideProgressDialog();
                        Intent intent = CertificateActivity.newIntent(getContext(), uuid);
                        startActivity(intent);
                        getActivity().finish();
                    }, throwable -> {
                        Timber.e(throwable, "Importing failed with error");
                        displayErrors(throwable, DialogUtils.ErrorCategory.CERTIFICATE, R.string.error_title_message);
                    });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void selectFile(Uri uri) {
        if (uri == null) {
            Timber.e("Selected file is null");
            return;
        }

        mSelectedFileUri = uri;
        Timber.d("File selected: %1$s", mSelectedFileUri.getPath());
        ContentResolver resolver = getContext().getContentResolver();
        try (Cursor cursor = resolver.query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                String filename = cursor.getString(nameIndex);
                mBinding.chooseFileButton.setText(filename);
            }
        }
        mBinding.importButton.setEnabled(true);
    }

    private void selectFile(File file) {
        if (file == null) {
            Timber.e("Selected file is null");
            return;
        }

        mSelectedFileUri = Uri.fromFile(file);
        Timber.d("File selected: %1$s", file.getAbsolutePath());

        String filename = file.getName();
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

    private static class JsonFilter implements FileFilter {
        @Override
        public boolean accept(File file) {
            String path = file.getAbsolutePath()
                    .toLowerCase();
            return path.endsWith(".json");
        }
    }

    private static class FileArrayAdapter extends ArrayAdapter<File> {

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

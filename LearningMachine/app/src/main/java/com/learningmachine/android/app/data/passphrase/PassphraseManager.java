package com.learningmachine.android.app.data.passphrase;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.Settings;

import androidx.annotation.RequiresApi;

import com.learningmachine.android.app.util.AESCrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.util.Scanner;

import timber.log.Timber;

public class PassphraseManager {
    private final Context mContext;
    private String mPassphrase;
    private PassphraseCallback mCallback;

    @FunctionalInterface
    public interface PassphraseCallback {
        void apply (String a);
    }

    public PassphraseManager(Context context) {
        mContext = context;
    }

    private Uri getLegacyPassphraseFileUri() {
        return Uri.parse(Environment.getExternalStorageDirectory() + "/learningmachine.dat");
    }

    public boolean doesLegacyPassphraseFileExist() {
        Uri passphraseUri = getLegacyPassphraseFileUri();
        if (passphraseUri != null && passphraseUri.getPath() != null) {
            return new File(passphraseUri.getPath()).exists();
        }
        return false;
    }

    public boolean canAccessLegacyPassphraseFile() {
        boolean canAccessFile = false;
        if (doesLegacyPassphraseFileExist()) {
            Uri passphraseFile = getLegacyPassphraseFileUri();
            try (FileInputStream inputStream = new FileInputStream(passphraseFile.getPath())) {
                canAccessFile = true;
            } catch (IOException e) {
                Timber.e(e, "Failed to retrieve passphrase backup");
            }
        }
        return canAccessFile;
    }

    public void savePassphraseInLegacyStorage(String passphrase, PassphraseCallback passphraseCallback) {
        Uri passphraseFile = getLegacyPassphraseFileUri();
        try (PrintWriter out = new PrintWriter(passphraseFile.getPath())) {
            storePassphraseBackup(passphrase, out, passphraseCallback);
        } catch (IOException e) {
            Timber.e(e, "Failed to save passphrase backup");
            passphraseCallback.apply(null);
        }
    }

    public void getLegacyPassphraseFromDevice(PassphraseCallback passphraseCallback) {
        Uri passphraseFile = getLegacyPassphraseFileUri();
        try (FileInputStream inputStream = new FileInputStream(passphraseFile.getPath())) {
            getPassphraseFromDevice(inputStream, passphraseCallback);
        } catch (IOException e) {
            Timber.e(e, "Failed to retrieve passphrase backup");
            passphraseCallback.apply(null);
        }
    }

    public void handleCanceledRequest() {
        mCallback.apply(null);
        cleanupPassphraseBackup();
    }

    private String getDeviceId() {
        @SuppressLint("HardwareIds")
        final String deviceId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        if(deviceId == null || deviceId.length() == 0) {
            return "NOT_IDEAL_KEY";
        }
        return deviceId;
    }

    public void initPassphraseBackup(String passphrase, PassphraseCallback callback) {
        mPassphrase = passphrase;
        mCallback = callback;
    }

    public void storePassphraseBackup(Uri location)  {
        ContentResolver resolver = mContext.getContentResolver();
        try (OutputStream outputStream = resolver.openOutputStream(location)) {
            PrintWriter out = new PrintWriter(outputStream);
            storePassphraseBackup(mPassphrase, out, mCallback);
        } catch (IOException e) {
            Timber.e(e);
            mCallback.apply(null);
        }
        cleanupPassphraseBackup();
    }

    private void storePassphraseBackup(String passphrase, PrintWriter out, PassphraseCallback callback) {
        if (passphrase == null) {
            Timber.e("Passphrase is null, cannot store backup.");
            callback.apply(null);
            return;
        }

        String encryptionKey= getDeviceId();
        String mneumonicString = "mneumonic:"+passphrase;
        try {
            Timber.i("Storing passphrase backup with encryption.");
            String encryptedMsg = AESCrypt.encrypt(encryptionKey, mneumonicString);
            out.println(encryptedMsg);
            out.flush();
            out.close();
            Timber.i("Passphrase backup stored successfully. Calling callback.");
            callback.apply(passphrase);
        } catch (GeneralSecurityException e){
            Timber.e(e, "Could not encrypt passphrase.");
            callback.apply(null);
        }
    }

    private void cleanupPassphraseBackup() {
        mPassphrase = null;
        mCallback = null;
    }

    public void initRestoreBackup(PassphraseCallback callback) {
        mCallback = callback;
    }

    public void getPassphraseBackup(Uri passphraseFile) {
        ContentResolver resolver = mContext.getContentResolver();
        try (InputStream inputStream = resolver.openInputStream(passphraseFile)) {
            getPassphraseFromDevice(inputStream, mCallback);
        } catch (IOException e) {
            Timber.e(e, "Failed to retrieve passphrase backup");
            mCallback.apply(null);
        }
        cleanupPassphraseBackup();
    }

    private void getPassphraseFromDevice(InputStream inputStream, PassphraseCallback passphraseCallback) {
        try {
            String encryptedMsg = new Scanner(inputStream).useDelimiter("\\Z").next();
            String encryptionKey = getDeviceId();
            try {
                String content = AESCrypt.decrypt(encryptionKey, encryptedMsg);
                if (content.startsWith("mneumonic:")) {
                    passphraseCallback.apply(content.substring(10).trim());
                    return;
                }
            }catch (GeneralSecurityException e){
                Timber.e(e, "Could not decrypt passphrase.");
            }
        } catch(Exception e) {
            // note: this is a non-critical feature, so if this fails nbd
            Timber.e(e, "Could not read passphrase file.");
        }

        passphraseCallback.apply(null);
    }
}

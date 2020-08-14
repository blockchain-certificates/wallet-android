package com.learningmachine.android.app.data.passphrase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;

import com.learningmachine.android.app.util.AESCrypt;

import java.io.File;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.util.Scanner;

import timber.log.Timber;

public class PassphraseManager {
    private final Context mContext;

    @FunctionalInterface
    public interface Callback <A, R> {
        R apply(A a);
    }

    public PassphraseManager(Context context) {
        mContext = context;
    }

    private String getPassphraseFilePath() {
        return MediaStore.Files.getContentUri("external").getPath() + "/learningmachine.dat";
    }

    private String getLegacyPassphraseFilePath() {
        return Environment.getExternalStorageDirectory() + "/learningmachine.dat";
    }

    public boolean doesLegacyPassphraseFileExist() {
        String passphraseFilePath = getLegacyPassphraseFilePath();
        return passphraseFilePath != null && new File(passphraseFilePath).exists();
    }

    public void migrateSavedPassphrase(Callback passphraseCallback) {
        getLegacyPassphraseFromDevice((passphrase) -> {
            savePassphraseToDevice((String)passphrase, passphraseCallback);
            File legacyFile = new File(getLegacyPassphraseFilePath());
            legacyFile.delete();
            return null;
        });
    }

    public void reset() {
        File file = new File(getPassphraseFilePath());
        file.delete();
    }
    private String getDeviceId(Context context) {
        @SuppressLint("HardwareIds")
        final String deviceId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        if(deviceId == null || deviceId.length() == 0) {
            return "NOT_IDEAL_KEY";
        }
        return deviceId;
    }

    public void savePassphraseToDevice(String passphrase, Callback passphraseCallback) {
        if (passphrase == null) {
            passphraseCallback.apply(null);
            return;
        }

        String passphraseFile = getPassphraseFilePath();
        try(PrintWriter out = new PrintWriter(passphraseFile)) {
            String encryptionKey= getDeviceId(mContext);
            String mneumonicString = "mneumonic:"+passphrase;
            try {
                String encryptedMsg = AESCrypt.encrypt(encryptionKey, mneumonicString);
                out.println(encryptedMsg);
                passphraseCallback.apply(passphrase);
            }catch (GeneralSecurityException e){
                Timber.e(e, "Could not encrypt passphrase.");
                passphraseCallback.apply(null);
            }
        } catch(Exception e) {
            Timber.e(e, "Could not write to passphrase file");
            passphraseCallback.apply(null);
        }
    }

    private boolean getLegacyPassphraseFromDevice(Callback passphraseCallback) {
        String passphraseFile = getLegacyPassphraseFilePath();
        return getPassphraseFromDevice(passphraseFile, passphraseCallback);
    }

    public boolean getSavedPassphraseFromDevice(Callback passphraseCallback) {
        String passphraseFile = getPassphraseFilePath();
        return getPassphraseFromDevice(passphraseFile, passphraseCallback);
    }

    private boolean getPassphraseFromDevice(String passphraseFile, Callback passphraseCallback) {
        try {
            String encryptedMsg = new Scanner(passphraseFile).useDelimiter("\\Z").next();
            String encryptionKey = getDeviceId(mContext);
            try {
                String content = AESCrypt.decrypt(encryptionKey, encryptedMsg);
                if (content.startsWith("mneumonic:")) {
                    passphraseCallback.apply(content.substring(10).trim());
                    return true;
                }
            }catch (GeneralSecurityException e){
                Timber.e(e, "Could not decrypt passphrase.");
            }
        } catch(Exception e) {
            // note: this is a non-critical feature, so if this fails nbd
            Timber.e(e, "Could not read passphrase file.");
        }

        passphraseCallback.apply(null);
        return false;
    }
}

package com.learningmachine.android.app.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.renderscript.ScriptGroup;

import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import timber.log.Timber;

public class BitcoinUtils {

    public static List<String> generateMnemonic(Context context, byte[] seedData) {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("english.txt");
            MnemonicCode mnemonicCode = new MnemonicCode(inputStream, null);
            return mnemonicCode.toMnemonic(seedData);
        } catch (IOException e) {
            Timber.e(e, "Unable to read word list.");
            e.printStackTrace();
        } catch (MnemonicException.MnemonicLengthException e) {
            Timber.e(e, "Unable to create mnemonic from word list");
            e.printStackTrace();
        }
        return null;
    }
}

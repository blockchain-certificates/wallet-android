package com.learningmachine.android.app.util;

import android.content.Context;
import android.content.res.AssetManager;

import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import timber.log.Timber;

public class BitcoinUtils {
    private static final String BIP39_ENGLISH_SHA256 = "ad90bf3beb7b0eb7e5acd74727dc0da96e0a280a258354e7293fb7e211ac03db";

    public static void init(Context context) {
        if (MnemonicCode.INSTANCE == null) {
            try {
                AssetManager assetManager = context.getAssets();
                InputStream inputStream = assetManager.open("english.txt");
                MnemonicCode.INSTANCE = new MnemonicCode(inputStream, BIP39_ENGLISH_SHA256);
            } catch (IOException e) {
                Timber.e(e, "Unable to read word list.");
            }
        }
    }

    public static List<String> generateMnemonic(Context context, byte[] seedData) {
        if (MnemonicCode.INSTANCE == null) {
            return null;
        }
        try {
            return MnemonicCode.INSTANCE.toMnemonic(seedData);
        } catch (MnemonicException.MnemonicLengthException e) {
            Timber.e(e, "Unable to create mnemonic from word list");
        }
        return null;
    }
}

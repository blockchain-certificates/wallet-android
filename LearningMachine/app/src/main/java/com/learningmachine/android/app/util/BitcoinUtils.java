package com.learningmachine.android.app.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.learningmachine.android.app.LMConstants;
import com.learningmachine.android.app.data.bitcoin.BIP44AccountZeroKeyChain;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.KeyChainGroup;
import org.bitcoinj.wallet.Wallet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
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

    public static List<String> generateMnemonic(byte[] seedData) {
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

    public static Wallet createWallet(NetworkParameters params, String seedPhrase) {
        byte[] entropy;
        try {
            entropy = MnemonicCode.INSTANCE.toEntropy(Arrays.asList(seedPhrase.split(" ")));
        } catch (MnemonicException e) {
            Timber.e(e, "Could not convert passphrase to entropy");
            return null;
        }
        return createWallet(params, entropy);
    }

    @NonNull
    public static Wallet createWallet(NetworkParameters params, byte[] entropy) {

        DeterministicSeed deterministicSeed = new DeterministicSeed(entropy,
                LMConstants.WALLET_PASSPHRASE,
                LMConstants.WALLET_CREATION_TIME_SECONDS);
        KeyChainGroup keyChainGroup = new KeyChainGroup(params, deterministicSeed);
        keyChainGroup.addAndActivateHDChain(new BIP44AccountZeroKeyChain(deterministicSeed));
        // m/44'/0'/0'/0
        ImmutableList BIP44_PATH = ImmutableList.of(new ChildNumber(44, true),
                new ChildNumber(0, true),
                new ChildNumber(0, false),
                new ChildNumber(0, false));

        return Wallet.fromSeed(params, deterministicSeed, BIP44_PATH);
    }

    public static boolean isValidPassphrase(String passphrase) {
        try {
            byte[] entropy = MnemonicCode.INSTANCE.toEntropy(Arrays.asList(passphrase.split(" ")));
            return entropy.length > 0;
        } catch (MnemonicException e) {
            Timber.e(e, "Invalid passphrase");
            return false;
        }
    }
}

package com.learningmachine.android.app.data.bitcoin;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.learningmachine.android.app.LMConstants;
import com.learningmachine.android.app.LMNetworkConstants;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.KeyChainGroup;
import org.bitcoinj.wallet.Wallet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.List;

import timber.log.Timber;

public class BitcoinManager {

    private Context mContext;
    private Wallet mWallet;

    public BitcoinManager(Context context) {
        mContext = context;
//        setupWalletAppKit();
        setupWallet();
    }

    private void setupWalletAppKit() {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + "/bitcoin/");
        WalletAppKit kit = new WalletAppKit(LMNetworkConstants.getNetwork(), path, LMConstants.WALLET_FILE) {
            @Override
            protected void onSetupCompleted() {
                super.onSetupCompleted();
                mWallet = wallet();
                DeterministicSeed seed = mWallet.getKeyChainSeed();
                List<String> mnemonicCode = seed.getMnemonicCode();
                String passphrase = TextUtils.join(" ", mnemonicCode);
                Timber.d("Passphrase: %1$s", passphrase);
            }
        };
        kit.setAutoSave(true);
        kit.startAsync();
    }

    private void setupWallet() {
        SecureRandom random = new SecureRandom();
        byte[] seedData = random.generateSeed(32);
        List<String> passphrase= generateMnemonicCode(seedData);
        String passphraseString = TextUtils.join(" ", passphrase);
        Timber.d("Before wallet: %1$s", passphraseString);

        DeterministicSeed deterministicSeed = new DeterministicSeed(passphrase, seedData, "", 0);
        NetworkParameters networkParameters = LMNetworkConstants.getNetwork();
        KeyChainGroup keyChainGroup = new KeyChainGroup(networkParameters, deterministicSeed);

        Wallet wallet = new Wallet(networkParameters, keyChainGroup);

        passphraseString = TextUtils.join(" ", wallet.getKeyChainSeed().getMnemonicCode());
        Timber.d("After wallet: %1$s", passphraseString);
    }

    private List<String> generateMnemonicCode(byte[] seedData) {
        try {
            InputStream inputStream = mContext.getAssets()
                    .open("english.txt");
            MnemonicCode mnemonicCode = new MnemonicCode(inputStream, null);
            List<String> words = mnemonicCode.toMnemonic(seedData);
            return words;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MnemonicException.MnemonicLengthException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getPassphrase() {
        if (mWallet == null) {
            return null;
        }
        DeterministicSeed seed = mWallet.getKeyChainSeed();
        List<String> mnemonicCode = seed.getMnemonicCode();
        return TextUtils.join(" ", mnemonicCode);
    }
}

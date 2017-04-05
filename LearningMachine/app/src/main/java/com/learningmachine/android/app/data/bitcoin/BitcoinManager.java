package com.learningmachine.android.app.data.bitcoin;

import android.content.Context;

import com.learningmachine.android.app.LMNetworkConstants;
import com.learningmachine.android.app.util.StringUtils;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.KeyChainGroup;
import org.bitcoinj.wallet.Wallet;

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
        createWallet();
    }

    private void createWallet() {
        SecureRandom random = new SecureRandom();
        byte[] seedData = random.generateSeed(32);
        List<String> mnemonic = generateMnemonic(seedData);
        if (mnemonic == null) {
            Timber.e("Mnemonic is null, wallet creation failure");
            return;
        }
        DeterministicSeed deterministicSeed = new DeterministicSeed(mnemonic, seedData, "", 0);
        NetworkParameters networkParameters = LMNetworkConstants.getNetwork();
        KeyChainGroup keyChainGroup = new KeyChainGroup(networkParameters, deterministicSeed);

        mWallet = new Wallet(networkParameters, keyChainGroup);
        // write wallet to file
    }

    private List<String> generateMnemonic(byte[] seedData) {
        try {
            InputStream inputStream = mContext.getAssets()
                    .open("english.txt");
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

    public String getPassphrase() {
        if (mWallet == null) {
            return null;
        }
        DeterministicSeed seed = mWallet.getKeyChainSeed();
        List<String> mnemonicCode = seed.getMnemonicCode();
        return StringUtils.join(" ", mnemonicCode);
    }
}

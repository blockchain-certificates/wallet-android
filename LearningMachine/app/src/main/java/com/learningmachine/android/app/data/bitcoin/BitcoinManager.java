package com.learningmachine.android.app.data.bitcoin;

import android.content.Context;

import com.learningmachine.android.app.LMNetworkConstants;
import com.learningmachine.android.app.util.ListUtils;
import com.learningmachine.android.app.util.StringUtils;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.KeyChainGroup;
import org.bitcoinj.wallet.Wallet;

import java.security.SecureRandom;
import java.util.List;

import timber.log.Timber;

import static com.learningmachine.android.app.util.BitcoinUtils.generateMnemonic;

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
        List<String> mnemonic = generateMnemonic(mContext, seedData);
        if (ListUtils.isEmpty(mnemonic)) {
            Timber.e("No mnemonic, wallet creation failure");
            return;
        }
        DeterministicSeed deterministicSeed = new DeterministicSeed(mnemonic, seedData, "", 0);
        NetworkParameters productionNetworkParameters = LMNetworkConstants.getProductionNetwork();
        KeyChainGroup keyChainGroup = new KeyChainGroup(productionNetworkParameters, deterministicSeed);

        mWallet = new Wallet(productionNetworkParameters, keyChainGroup);
        // write wallet to file
    }

    public String getPassphrase() {
        if (mWallet == null) {
            return null;
        }
        DeterministicSeed seed = mWallet.getKeyChainSeed();
        List<String> mnemonicCode = seed.getMnemonicCode();
        return StringUtils.join(" ", mnemonicCode);
    }

    public String getBitcoinAddress() {
        if (mWallet == null) {
            return null;
        }

        return mWallet.currentReceiveAddress().toString();
    }
}

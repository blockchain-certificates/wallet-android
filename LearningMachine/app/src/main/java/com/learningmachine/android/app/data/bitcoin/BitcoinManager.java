package com.learningmachine.android.app.data.bitcoin;

import android.content.Context;

import com.learningmachine.android.app.LMNetworkConstants;
import com.learningmachine.android.app.util.ListUtils;
import com.learningmachine.android.app.util.StringUtils;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.KeyChainGroup;
import org.bitcoinj.wallet.Protos;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.WalletExtension;
import org.bitcoinj.wallet.WalletProtobufSerializer;

import java.io.FileInputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
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
        NetworkParameters networkParameters = LMNetworkConstants.getNetwork();
        KeyChainGroup keyChainGroup = new KeyChainGroup(networkParameters, deterministicSeed);

        mWallet = new Wallet(networkParameters, keyChainGroup);
        // write wallet to file
    }

    private void DELETE_ME() {
//        WalletAppKit walletAppKit = new WalletAppKit();
    }

    private Wallet loadWallet() throws Exception {
        // check wallet exists
        Wallet wallet;
        FileInputStream walletStream = new FileInputStream("whatever.wallet");
        try {
            WalletExtension[] extensions = {};
            Protos.Wallet proto = WalletProtobufSerializer.parseToProto(walletStream);
            WalletProtobufSerializer serializer = new WalletProtobufSerializer();
            NetworkParameters networkParameters = LMNetworkConstants.getNetwork();
            wallet = serializer.readWallet(networkParameters, extensions, proto);
        } finally {
            walletStream.close();
        }
        return wallet;
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

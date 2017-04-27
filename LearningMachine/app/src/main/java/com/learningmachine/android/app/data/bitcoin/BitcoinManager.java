package com.learningmachine.android.app.data.bitcoin;

import android.content.Context;
import android.support.annotation.VisibleForTesting;

import com.learningmachine.android.app.LMConstants;
import com.learningmachine.android.app.LMNetworkConstants;
import com.learningmachine.android.app.util.ListUtils;
import com.learningmachine.android.app.util.StringUtils;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.KeyChainGroup;
import org.bitcoinj.wallet.Protos;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.WalletExtension;
import org.bitcoinj.wallet.WalletProtobufSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;

import timber.log.Timber;

import static com.learningmachine.android.app.util.BitcoinUtils.generateMnemonic;

public class BitcoinManager {

    private static final String PASSPHRASE_DELIMETER = " ";

    private Context mContext;
    private Wallet mWallet;

    public BitcoinManager(Context context) {
        mContext = context;
        setup();
    }

    private void setup() {
        boolean walletLoaded = false;
        if (getWalletFile().exists()) {
            Timber.d("Wallet exists, attempting to load");
            walletLoaded = loadWallet();
        }

        if (!walletLoaded) {
            Timber.d("Wallet not loaded, creating a new one");
            createWallet();
        }
    }

    @VisibleForTesting
    protected File getWalletFile() {
        return new File(mContext.getFilesDir(), LMConstants.WALLET_FILE);
    }

    private void createWallet() {
        SecureRandom random = new SecureRandom();
        byte[] seedData = random.generateSeed(LMConstants.WALLET_SEED_BYTE_SIZE);
        List<String> mnemonic = generateMnemonic(mContext, seedData);
        if (ListUtils.isEmpty(mnemonic)) {
            Timber.e("No mnemonic, wallet creation failure");
            return;
        }

        buildWallet(mnemonic, seedData);
    }

    private void buildWallet(List<String> mnemonic, byte[] seedData) {
        DeterministicSeed deterministicSeed = new DeterministicSeed(mnemonic,
                seedData,
                LMConstants.WALLET_PASSPHRASE,
                LMConstants.WALLET_CREATION_TIME_SECONDS);
        NetworkParameters networkParameters = LMNetworkConstants.getNetwork();
        KeyChainGroup keyChainGroup = new KeyChainGroup(networkParameters, deterministicSeed);
        mWallet = new Wallet(networkParameters, keyChainGroup);
        saveWallet();
    }

    /**
     * @return true if wallet was loaded successfully
     */
    private boolean loadWallet() {
        try (FileInputStream walletStream = new FileInputStream(getWalletFile())) {
            WalletExtension[] extensions = {};
            Protos.Wallet proto = WalletProtobufSerializer.parseToProto(walletStream);
            WalletProtobufSerializer serializer = new WalletProtobufSerializer();
            NetworkParameters networkParameters = LMNetworkConstants.getNetwork();
            mWallet = serializer.readWallet(networkParameters, extensions, proto);
            Timber.d("Wallet successfully loaded");
            return true;
        } catch (UnreadableWalletException e) {
            Timber.e(e, "Wallet is corrupted");
        } catch (FileNotFoundException e) {
            Timber.e(e, "Wallet file not found");
        } catch (IOException e) {
            Timber.e(e, "Wallet unable to be parsed");
        }

        Timber.e("Wallet not loaded, something went wrong");
        return false;
    }

    /**
     * @return true if wallet was saved successfully
     */
    private boolean saveWallet() {
        if (mWallet == null) {
            Timber.e(new Exception(), "Wallet doesn't exit");
            return false;
        }
        try {
            mWallet.saveToFile(getWalletFile());
            Timber.d("Wallet successfully saved");
            return true;
        } catch (IOException e) {
            Timber.e(e, "Unable to save Wallet");
        }

        return false;
    }

    public String getPassphrase() {
        if (mWallet == null) {
            return null;
        }
        DeterministicSeed seed = mWallet.getKeyChainSeed();
        List<String> mnemonicCode = seed.getMnemonicCode();
        return StringUtils.join(PASSPHRASE_DELIMETER, mnemonicCode);
    }

    public void setPassphrase(String newPassphrase) {
        if (StringUtils.isEmpty(newPassphrase)) {
            return;
        }
        List<String> newPassphraseList = StringUtils.split(newPassphrase, PASSPHRASE_DELIMETER);
        buildWallet(newPassphraseList, null);
    }
}

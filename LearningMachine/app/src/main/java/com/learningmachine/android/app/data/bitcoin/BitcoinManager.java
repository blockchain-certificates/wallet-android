package com.learningmachine.android.app.data.bitcoin;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.VisibleForTesting;
import android.util.Pair;

import com.learningmachine.android.app.LMConstants;
import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.error.ExceptionWithResourceString;
import com.learningmachine.android.app.data.preferences.SharedPreferencesManager;
import com.learningmachine.android.app.data.store.CertificateStore;
import com.learningmachine.android.app.data.store.IssuerStore;
import com.learningmachine.android.app.util.BitcoinUtils;
import com.learningmachine.android.app.util.StringUtils;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;

import rx.Observable;
import timber.log.Timber;

public class BitcoinManager {

    private static final String PASSPHRASE_DELIMETER = " ";

    private final Context mContext;
    private final NetworkParameters mNetworkParameters;
    private final IssuerStore mIssuerStore;
    private final CertificateStore mCertificateStore;
    private final SharedPreferencesManager mSharedPreferencesManager;
    private Wallet mWallet;

    public BitcoinManager(Context context, NetworkParameters networkParameters, IssuerStore issuerStore, CertificateStore certificateStore, SharedPreferencesManager sharedPreferencesManager) {
        mContext = context;
        mNetworkParameters = networkParameters;
        mIssuerStore = issuerStore;
        mCertificateStore = certificateStore;
        mSharedPreferencesManager = sharedPreferencesManager;
    }

    private Observable<Wallet> getWallet() {
        return Observable.defer(() -> {
            if (mWallet != null) {
                return Observable.just(mWallet);
            }
            if (getWalletFile().exists()) {
                return loadWallet();
            } else {
                return createWallet();
            }
        });
    }

    @VisibleForTesting
    protected File getWalletFile() {
        return new File(mContext.getFilesDir(), LMConstants.WALLET_FILE);
    }

    public SharedPreferencesManager getSharedPreferences() {
        return mSharedPreferencesManager;
    }

    private Observable<Wallet> createWallet() {
        SecureRandom random = new SecureRandom();
        byte[] entropy = random.generateSeed(LMConstants.WALLET_SEED_BYTE_SIZE);
        buildWallet(entropy);
        return Observable.just(mWallet);
    }

    private Observable<Wallet> buildWallet(byte[] entropy) {
        mWallet = BitcoinUtils.createWallet(mNetworkParameters, entropy);
        return saveWallet();
    }

    private Observable<Wallet> buildWallet(String seedPhrase) {
        mWallet = BitcoinUtils.createWallet(mNetworkParameters, seedPhrase);
        return saveWallet();
    }

    /**
     * @return true if wallet was loaded successfully
     */
    private Observable<Wallet> loadWallet() {
        try (FileInputStream walletStream = new FileInputStream(getWalletFile())) {
            Wallet wallet = BitcoinUtils.loadWallet(walletStream, mNetworkParameters);
            if (BitcoinUtils.updateRequired(wallet)) {
                Address currentReceiveAddress = wallet.currentReceiveAddress();
                mSharedPreferencesManager.setLegacyReceiveAddress(currentReceiveAddress.toString());
                wallet = BitcoinUtils.updateWallet(wallet);
                wallet.saveToFile(getWalletFile());
                Timber.d("Wallet successfully updated");
            }
            mWallet = wallet;
            Timber.d("Wallet successfully loaded");
            return Observable.just(mWallet);
        } catch (UnreadableWalletException e) {
            Timber.e(e, "Wallet is corrupted");
            return Observable.error(e);
        } catch (FileNotFoundException e) {
            Timber.e(e, "Wallet file not found");
            return Observable.error(e);
        } catch (IOException e) {
            Timber.e(e, "Wallet unable to be parsed");
            return Observable.error(e);
        }
    }

    /**
     * @return true if wallet was saved successfully
     */
    private Observable<Wallet> saveWallet() {
        if (mWallet == null) {
            Exception e = new Exception("Wallet doesn't exist");
            Timber.e(e, "Wallet doesn't exist");
            return Observable.error(e);
        }
        try {
            mWallet.saveToFile(getWalletFile());
            Timber.d("Wallet successfully saved");
            return Observable.just(mWallet);
        } catch (IOException e) {
            Timber.e(e, "Unable to save Wallet");
            return Observable.error(e);
        }
    }

    public Observable<String> getPassphrase() {
        return getWallet().map(wallet -> {
            DeterministicSeed seed = wallet.getKeyChainSeed();
            List<String> mnemonicCode = seed.getMnemonicCode();
            return StringUtils.join(PASSPHRASE_DELIMETER, mnemonicCode);
        });
    }

    public void resetEverything() {
        mIssuerStore.reset();
        mCertificateStore.reset();

        String passphraseFileOnExternalStorage = Environment.getExternalStorageDirectory() + "/learningmachine.dat";
        File file = new File(passphraseFileOnExternalStorage);
        file.delete();
    }

    public Observable<Wallet> setPassphrase(String newPassphrase) {
        if (StringUtils.isEmpty(newPassphrase) || !BitcoinUtils.isValidPassphrase(newPassphrase)) {
            return Observable.error(new ExceptionWithResourceString(R.string.error_invalid_passphrase_malformed));
        }
        mIssuerStore.reset();
        mCertificateStore.reset();
        return buildWallet(newPassphrase);
    }

    public Observable<String> getCurrentBitcoinAddress() {
        return getWallet().map(wallet -> wallet.currentReceiveAddress().toString());
    }

    public Observable<String> getFreshBitcoinAddress() {
        return getWallet().map(wallet -> wallet.freshReceiveAddress().toString())
                .flatMap(address -> Observable.combineLatest(Observable.just(address), saveWallet(), Pair::new))
                .map(pair -> pair.first);
    }

    public boolean isMyIssuedAddress(String addressString) {
        String legacyReceiveAddress = mSharedPreferencesManager.getLegacyReceiveAddress();
        if (legacyReceiveAddress != null) {
            if (legacyReceiveAddress.equals(addressString)) {
                return true;
            }
        }
        Address address = Address.fromBase58(mNetworkParameters, addressString);
        return mWallet.getIssuedReceiveAddresses().contains(address);
    }
}

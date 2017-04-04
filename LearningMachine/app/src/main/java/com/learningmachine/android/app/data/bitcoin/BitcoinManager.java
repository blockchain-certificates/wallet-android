package com.learningmachine.android.app.data.bitcoin;

import android.os.Environment;
import android.text.TextUtils;

import com.learningmachine.android.app.LMConstants;
import com.learningmachine.android.app.LMNetworkConstants;

import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.Wallet;

import java.io.File;
import java.util.List;

import timber.log.Timber;

public class BitcoinManager {

    private WalletAppKit mWalletAppKit;
    private Wallet mWallet;

    public BitcoinManager() {
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

    public String getPassphrase() {
        if (mWallet == null) {
            return null;
        }
        DeterministicSeed seed = mWallet.getKeyChainSeed();
        List<String> mnemonicCode = seed.getMnemonicCode();
        return TextUtils.join(" ", mnemonicCode);
    }
}

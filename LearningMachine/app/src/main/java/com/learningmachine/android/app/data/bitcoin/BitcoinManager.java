package com.learningmachine.android.app.data.bitcoin;

import android.os.Environment;
import android.text.TextUtils;

import com.google.common.util.concurrent.Service;
import com.learningmachine.android.app.LMConstants;
import com.learningmachine.android.app.LMNetworkConstants;

import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletChangeEventListener;

import java.io.File;
import java.util.List;

public class BitcoinManager {

    /*
        static func generateSeedPhrase() -> String {
            let randomData = BTCRandomDataWithLength(32) as Data
            return generateSeedPhrase(withRandomData: randomData)
        }

        static func generateSeedPhrase(withRandomData randomData: Data) -> String {
            let mn = BTCMnemonic(entropy: randomData, password: "", wordListType: .english)

            return mn?.words.flatMap({ $0 as? String }).joined(separator: " ") ?? ""
        }
     */
    private WalletAppKit mWalletAppKit;
    private Wallet mWallet;

    public BitcoinManager() {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + "/bitcoin/");
        WalletAppKit kit = new WalletAppKit(LMNetworkConstants.getNetwork(), path, LMConstants.WALLET_FILE);
        kit.setAutoSave(true);
        kit.startAsync();
        kit.addListener(new Service.Listener() {
            @Override
            public void running() {
                super.running();
            }
        }, null);
//        mWallet.addChangeEventListener(new WalletChangeEventListener() {
//            @Override
//            public void onWalletChanged(Wallet wallet) {
//                wallet.get
//            }
//        });
//        kit.startAndWait();
        //        kit.awaitRunning();
        //      kit.wallet().reset();
//        peerGroup = kit.peerGroup();

        mWallet = kit.wallet();
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

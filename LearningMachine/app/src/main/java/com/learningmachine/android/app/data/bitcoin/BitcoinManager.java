package com.learningmachine.android.app.data.bitcoin;

import com.learningmachine.android.app.LMConstants;

import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.kits.WalletAppKit;

import java.io.File;

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
    public String generateSeedPhrase() {
//        BTCrandom
        MnemonicCode
    }

    public String generateSeedPhrase() {

        WalletAppKit kit = new WalletAppKit(LMConstants.getNetwork(), new File("."), SideConstants.WALLET_FILE);
        kit.setAutoSave(true);
        kit.startAndWait();
        //        kit.awaitRunning();
        //      kit.wallet().reset();
        peerGroup = kit.peerGroup();
        wallet = kit.wallet();
    }
}

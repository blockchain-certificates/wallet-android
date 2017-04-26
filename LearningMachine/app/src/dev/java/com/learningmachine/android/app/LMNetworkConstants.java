package com.learningmachine.android.app;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;

public class LMNetworkConstants {
    public static NetworkParameters getNetwork() {
        return TestNet3Params.get();
    }

    public static NetworkParameters getProductionNetwork() {
        return MainNetParams.get();
    }
}

package com.learningmachine.android.app.data.bitcoin;

import com.google.common.collect.ImmutableList;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;

public class BIP44AccountZeroKeyChain extends DeterministicKeyChain {

    public BIP44AccountZeroKeyChain(DeterministicSeed seed) {
        super(seed);
    }

    @Override
    protected ImmutableList<ChildNumber> getAccountPath() {
        return BIP44_ACCOUNT_ZERO_PATH;
    }
}

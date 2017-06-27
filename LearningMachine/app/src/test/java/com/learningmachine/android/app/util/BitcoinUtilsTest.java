package com.learningmachine.android.app.util;

import android.content.Context;
import android.content.res.AssetManager;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Utils;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.Wallet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

public class BitcoinUtilsTest {

    private Context mContext;

    @Before
    public void setup() throws Exception {
        mContext = Mockito.mock(Context.class);

        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("english.txt");

        AssetManager assetManager = Mockito.mock(AssetManager.class);
        Mockito.when(mContext.getAssets()).thenReturn(assetManager);
        Mockito.when(assetManager.open(any())).thenReturn(inputStream);
    }

    @Test
    public void testGenerateMnemonic() throws Exception {
        byte[] seedData = new byte[32]; // all zeros
        String expectedMnemonic = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon art";
        List<String> seedPhrase = BitcoinUtils.generateMnemonic(seedData);
        assertFalse(ListUtils.isEmpty(seedPhrase));

        String mnemonic = StringUtils.join(" ", seedPhrase);

        assertFalse("Mnemonic phrase should not be empty", mnemonic.isEmpty());
        assertEquals("0-seed should generate simple mnemonic phrase", expectedMnemonic, mnemonic);
    }

    @Test
    public void createWalletFromSeedPhrase() {
        String seedPhrase = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon art";
        NetworkParameters params = MainNetParams.get();
        Wallet wallet = BitcoinUtils.createWallet(params, seedPhrase);
        Address firstAddress = wallet.freshReceiveAddress();
        Address secondAddress = wallet.freshReceiveAddress();
        assertEquals("1KBdbBJRVYffWHWWZ1moECfdVBSEnDpLHi", firstAddress.toBase58());
        assertEquals("1EiJMaaahrhpbhgaNzMeUe1ZoiXdbBhWhR", secondAddress.toBase58());
    }

    @Test
    public void createWalletFromEntropy() {
        byte[] entropy = new byte[32]; // Utils.HEX.decode(bip39seed);
        NetworkParameters params = MainNetParams.get();
        Wallet wallet = BitcoinUtils.createWallet(params, entropy);
        Address firstAddress = wallet.freshReceiveAddress();
        Address secondAddress = wallet.freshReceiveAddress();
        assertEquals("1KBdbBJRVYffWHWWZ1moECfdVBSEnDpLHi", firstAddress.toBase58());
        assertEquals("1EiJMaaahrhpbhgaNzMeUe1ZoiXdbBhWhR", secondAddress.toBase58());
    }

    @Test
    public void keychainSeedMatches() {
        String bip39seed = "408b285c123836004f4b8842c89324c1f01382450c0d439af345ba7fc49acf705489c6fc77dbd4e3dc1dd8cc6bc9f043db8ada1e243c4a0eafb290d399480840";
        byte[] entropy = new byte[32];
        NetworkParameters params = MainNetParams.get();
        Wallet wallet = BitcoinUtils.createWallet(params, entropy);
        DeterministicSeed keyChainSeed = wallet.getKeyChainSeed();
        byte[] seedBytes = keyChainSeed.getSeedBytes();
        String encodedSeedBytes = Utils.HEX.encode(seedBytes);
        assertEquals(bip39seed, encodedSeedBytes);
    }

    @Test
    public void detectsValidPassphrases() {
        String seedPhrase = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon art";
        assertTrue("This should be a valid phrase", BitcoinUtils.isValidPassphrase(seedPhrase));
    }

    @Test
    public void detectsInvalidPassphrases() {
        String seedPhrase = "This phrase is too short and not random enough";
        assertFalse("This should be an invalid phrase", BitcoinUtils.isValidPassphrase(seedPhrase));
    }
}

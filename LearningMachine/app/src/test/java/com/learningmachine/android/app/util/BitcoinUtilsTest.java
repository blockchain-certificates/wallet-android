package com.learningmachine.android.app.util;

import android.content.Context;
import android.content.res.AssetManager;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Utils;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

public class BitcoinUtilsTest {

    private static final String ABANDON_ABANDON_ART = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon art";
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
        String expectedMnemonic = ABANDON_ABANDON_ART;
        List<String> seedPhrase = BitcoinUtils.generateMnemonic(seedData);
        assertFalse(ListUtils.isEmpty(seedPhrase));

        String mnemonic = StringUtils.join(" ", seedPhrase);

        assertFalse("Mnemonic phrase should not be empty", mnemonic.isEmpty());
        assertEquals("0-seed should generate simple mnemonic phrase", expectedMnemonic, mnemonic);
    }

    @Test
    public void createWalletFromSeedPhrase() {
        String seedPhrase = ABANDON_ABANDON_ART;
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
        String seedPhrase = ABANDON_ABANDON_ART;
        assertTrue("This should be a valid phrase", BitcoinUtils.isValidPassphrase(seedPhrase));
    }

    @Test
    public void detectsInvalidPassphrases() {
        String seedPhrase = "This phrase is too short and not random enough";
        assertFalse("This should be an invalid phrase", BitcoinUtils.isValidPassphrase(seedPhrase));
    }

    @Test
    public void pubKeyOwnershipConfirmed() {
        String pubKeyString = "03f5b5836e454ac540c912371d44b4bed1123be954644876a72571dc679f8e89d0";
        byte[] pubKey = Utils.HEX.decode(pubKeyString);
        String seedPhrase = ABANDON_ABANDON_ART;
        NetworkParameters params = MainNetParams.get();
        Wallet wallet = BitcoinUtils.createWallet(params, seedPhrase);
        assertTrue("Should be the owner of this pub key", wallet.isPubKeyMine(pubKey));
    }

    @Test
    public void pubKeyNonOwnershipDetected() {
        String pubKeyString = "444444444444444444444444444444444444444444444444444444444444444444";
        byte[] pubKey = Utils.HEX.decode(pubKeyString);
        String seedPhrase = ABANDON_ABANDON_ART;
        NetworkParameters params = MainNetParams.get();
        Wallet wallet = BitcoinUtils.createWallet(params, seedPhrase);
        assertFalse("Should not be the owner of this pub key", wallet.isPubKeyMine(pubKey));
    }

    @Test
    public void previousReceiveAddressesOwnershipVerified() {
        String seedPhrase = ABANDON_ABANDON_ART;
        NetworkParameters params = MainNetParams.get();
        Wallet wallet = BitcoinUtils.createWallet(params, seedPhrase);
        Address address1 = wallet.freshReceiveAddress();
        Address address2 = wallet.freshReceiveAddress();
        Address address3 = wallet.freshReceiveAddress();
        Address address = Address.fromBase58(params, "1H13uL5vSVvgPcJm16WjA6TPqGmouQaKtn");
        assertFalse("Should not contain the non-issued address", wallet.getIssuedReceiveAddresses().contains(address));
        Address address4 = wallet.freshReceiveAddress();
        assertTrue("Should contain the issued address", wallet.getIssuedReceiveAddresses().contains(address));
    }

    @Test
    public void savedWalletResumesFreshReceiveAddresses() throws IOException, UnreadableWalletException {
        String seedPhrase = ABANDON_ABANDON_ART;
        NetworkParameters params = MainNetParams.get();
        Wallet wallet = BitcoinUtils.createWallet(params, seedPhrase);
        File tempFile = File.createTempFile("temp", "wallet");
        Address address1 = wallet.freshReceiveAddress();
        Address address2 = wallet.freshReceiveAddress();
        wallet.saveToFile(tempFile);
        Wallet walletFromFile = Wallet.loadFromFile(tempFile);
        Address address3 = walletFromFile.freshReceiveAddress();
        Address address4 = walletFromFile.freshReceiveAddress();
        assertEquals("1KBdbBJRVYffWHWWZ1moECfdVBSEnDpLHi", address1.toBase58());
        assertEquals("1EiJMaaahrhpbhgaNzMeUe1ZoiXdbBhWhR", address2.toBase58());
        assertEquals("1AxcesYVBs6ddjcuXDLLBMkgYwjkh8mora", address3.toBase58());
        assertEquals("1H13uL5vSVvgPcJm16WjA6TPqGmouQaKtn", address4.toBase58());
    }
}

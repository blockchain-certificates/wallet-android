package com.learningmachine.android.app.util;

import android.content.Context;
import android.content.res.AssetManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
        List<String> seedPhrase = BitcoinUtils.generateMnemonic(mContext, seedData);
        assertFalse(ListUtils.isEmpty(seedPhrase));

        String mnemonic = StringUtils.join(" ", seedPhrase);

        assertFalse("Mnemonic phrase should not be empty", mnemonic.isEmpty());
        assertEquals("0-seed should generate simple mnemonic phrase", mnemonic, expectedMnemonic);
    }
}

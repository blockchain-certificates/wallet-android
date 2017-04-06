package com.learningmachine.android.app.data.bitcoin;

import android.content.Context;
import android.content.res.AssetManager;

import com.learningmachine.android.app.util.StringUtils;

import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BitcoinManagerTest {

    private Context mContext;
    private BitcoinManager mBitcoinManager;

    @Before
    public void setup() throws Exception {
        mContext = mock(Context.class);

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("english.txt");

        AssetManager assetManager = mock(AssetManager.class);
        when(mContext.getAssets()).thenReturn(assetManager);
        when(assetManager.open(any())).thenReturn(inputStream);
        mBitcoinManager = new BitcoinManager(mContext);
    }

    @Test
    public void testGenerateMnemonic() throws Exception {
        byte[] seedData = new byte[32]; // all zeros
        String expectedMnemonic = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon art";
        List<String> seedPhrase = mBitcoinManager.generateMnemonic(seedData);
        String mnemonic = StringUtils.join(" ", seedPhrase);

        assertFalse("Mnemonic phrase should not be empty", mnemonic.isEmpty());
        assertEquals("0-seed should generate simple mnemonic phrase", mnemonic, expectedMnemonic);
    }
}

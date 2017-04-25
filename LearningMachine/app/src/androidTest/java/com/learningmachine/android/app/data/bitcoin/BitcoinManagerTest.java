package com.learningmachine.android.app.data.bitcoin;


import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class BitcoinManagerTest {

    @Test
    public void walletShouldBeSaved_andLoaded() {
        Context context = InstrumentationRegistry.getTargetContext();
        BitcoinManager bitcoinManager = new BitcoinManager(context);

        assertTrue(bitcoinManager.getWalletFile()
                .exists());

        String firstPassphrase = bitcoinManager.getPassphrase();

        bitcoinManager = new BitcoinManager(context);

        assertTrue(bitcoinManager.getWalletFile()
                .exists());

        String secondPassphrase = bitcoinManager.getPassphrase();

        assertEquals(firstPassphrase, secondPassphrase);
    }

}

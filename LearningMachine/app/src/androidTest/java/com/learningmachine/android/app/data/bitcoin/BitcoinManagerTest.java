package com.hyland.android.app.data.bitcoin;


import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class BitcoinManagerTest {

    @Test
    public void walletShouldBeSaved_andLoaded() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        NetworkParameters networkParameters = MainNetParams.get();

        StringHolder stringHolder = new StringHolder();
        BitcoinManager firstBitcoinManager = new BitcoinManager(context, networkParameters, null, null, null);
        firstBitcoinManager.getPassphrase().subscribe(firstPassphrase -> {
            assertTrue(firstBitcoinManager.getWalletFile().exists());
            assertThat(firstPassphrase, not(isEmptyOrNullString()));
            stringHolder.string = firstPassphrase;
        });

        BitcoinManager secondBitcoinManager = new BitcoinManager(context, networkParameters, null, null, null);
        secondBitcoinManager.getPassphrase().subscribe(secondPassphrase -> {
            assertTrue(secondBitcoinManager.getWalletFile().exists());
            assertEquals(stringHolder.string, secondPassphrase);
        });
    }

    @Test
    public void walletShouldIssueNewReceiveAddressesAfterReload() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        NetworkParameters networkParameters = MainNetParams.get();

        StringHolder stringHolder = new StringHolder();
        BitcoinManager firstBitcoinManager = new BitcoinManager(context, networkParameters, null, null, null);
        firstBitcoinManager.getFreshBitcoinAddress().subscribe(firstReceiveAddress -> {
            assertThat(firstReceiveAddress, not(isEmptyOrNullString()));
            stringHolder.string = firstReceiveAddress;
        });

        BitcoinManager secondBitcoinManager = new BitcoinManager(context, networkParameters, null, null, null);
        secondBitcoinManager.getFreshBitcoinAddress().subscribe(secondReceiveAddress -> {
            assertNotEquals("Fresh receive address expected", stringHolder.string, secondReceiveAddress);
        });
    }

    static class StringHolder {
        String string;
    }
}

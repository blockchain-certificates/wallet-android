package com.learningmachine.android.app.data.inject;

import android.content.Context;
import android.provider.ContactsContract;

import com.learningmachine.android.app.data.bitcoin.BitcoinManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {

    @Provides
    @Singleton
    BitcoinManager providesBitcoinManager(Context context) {
        return new BitcoinManager(context);
    }
}

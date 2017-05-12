package com.learningmachine.android.app.data.inject;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;
import timber.log.Timber.DebugTree;

@Module(includes = DataModule.class)
public class StagingDataModule {

    @Provides
    @Singleton
    Timber.Tree provideLoggingTree() {
        return new DebugTree();
    }

    @Provides
    @Singleton
    NetworkParameters providesBitcoinNetworkParameters() {
        return MainNetParams.get();
    }

    @Provides
    HttpLoggingInterceptor.Level providesLogLevel() {
        return HttpLoggingInterceptor.Level.BASIC;
    }
}

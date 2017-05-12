package com.learningmachine.android.app.data.inject;

import com.learningmachine.android.app.data.log.NoLoggingTree;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

@Module(includes = DataModule.class)
public class ProductionDataModule {

    @Provides
    @Singleton
    Timber.Tree provideLoggingTree() {
        return new NoLoggingTree();
    }

    @Provides
    @Singleton
    NetworkParameters providesBitcoinNetworkParameters() {
        return MainNetParams.get();
    }

    @Provides
    HttpLoggingInterceptor.Level providesLogLevel() {
        return HttpLoggingInterceptor.Level.NONE;
    }
}

package com.learningmachine.android.app.data.inject;

import com.learningmachine.android.app.data.log.NoLoggingTree;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
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
        return TestNet3Params.get();
    }
}

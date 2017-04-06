package com.learningmachine.android.app.data.inject;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;
import timber.log.Timber.DebugTree;

@Module(includes = DataModule.class)
public class DevDataModule {

    @Provides
    @Singleton
    Timber.Tree provideLoggingTree() {
        return new DebugTree();
    }
}

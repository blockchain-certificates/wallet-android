package com.learningmachine.android.app.data.inject;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class LMModule {

    private Context mApplicationContext;

    public LMModule(Context applicationContext) {
        mApplicationContext = applicationContext;
    }

    @Singleton
    @Provides
    Context providesContext() {
        return mApplicationContext;
    }
}

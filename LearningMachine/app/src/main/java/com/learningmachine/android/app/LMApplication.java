package com.learningmachine.android.app;

import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;

import com.learningmachine.android.app.data.bitcoin.BitcoinManager;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.data.inject.LMComponent;
import com.learningmachine.android.app.data.inject.LMGraph;

import javax.inject.Inject;

import timber.log.Timber;

public class LMApplication extends MultiDexApplication {

    protected LMGraph mGraph;

    @Inject Timber.Tree mTree;
    @Inject BitcoinManager mBitcoinManager;

    @Override
    public void onCreate() {
        super.onCreate();

        setupDagger();
        setupTimber();
    }

    @Override
    public Object getSystemService(@NonNull String name) {
        if (Injector.matchesService(name)) {
            return mGraph;
        }
        return super.getSystemService(name);
    }

    private void setupDagger() {
        mGraph = LMComponent.Initializer.init(this);
        mGraph.inject(this);
    }

    private void setupTimber() {
        Timber.plant(mTree);
    }
}

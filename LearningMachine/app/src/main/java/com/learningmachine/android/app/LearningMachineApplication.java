package com.learningmachine.android.app;

import android.app.Application;

import com.learningmachine.android.app.data.inject.LMComponent;
import com.learningmachine.android.app.data.inject.LMGraph;

import javax.inject.Inject;

import timber.log.Timber;

public class LearningMachineApplication extends Application {

    protected LMGraph mGraph;

    @Inject Timber.Tree mTree;

    @Override
    public void onCreate() {
        super.onCreate();

        setupDagger();
        setupTimber();
    }

    private void setupDagger() {
        mGraph = LMComponent.Initializer.init();
        mGraph.inject(this);
    }

    private void setupTimber() {
        Timber.plant(mTree);
    }
}

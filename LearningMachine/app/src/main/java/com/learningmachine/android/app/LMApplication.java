package com.learningmachine.android.app;

import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;

import com.learningmachine.android.app.data.CertificateManager;
import com.learningmachine.android.app.data.IssuerManager;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.data.inject.LMComponent;
import com.learningmachine.android.app.data.inject.LMGraph;
import com.learningmachine.android.app.data.preferences.SharedPreferencesManager;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class LMApplication extends MultiDexApplication {

    protected LMGraph mGraph;

    @Inject Timber.Tree mTree;
    @Inject SharedPreferencesManager mPreferencesManager;
    @Inject IssuerManager mIssuerManager;
    @Inject CertificateManager mCertificateManager;

    @Override
    public void onCreate() {
        super.onCreate();

        setupDagger();
        setupTimber();
        setupJodaTime();
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

    protected void setupJodaTime() {
        JodaTimeAndroid.init(getApplicationContext());
    }
}

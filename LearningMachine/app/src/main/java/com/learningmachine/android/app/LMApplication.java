package com.learningmachine.android.app;

import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;
import android.webkit.WebView;

import com.bugsee.library.Bugsee;
import com.learningmachine.android.app.data.CertificateManager;
import com.learningmachine.android.app.data.IssuerManager;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.data.inject.LMComponent;
import com.learningmachine.android.app.data.inject.LMGraph;
import com.learningmachine.android.app.data.preferences.SharedPreferencesManager;
import com.learningmachine.android.app.util.BitcoinUtils;
import com.learningmachine.android.app.util.FileLoggingTree;

import net.danlew.android.joda.JodaTimeAndroid;

import javax.inject.Inject;

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
        enableWebDebugging();
        setupMnemonicCode();
        Timber.i("Application was launched!");
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
        Timber.plant(new FileLoggingTree());
    }

    protected void setupJodaTime() {
        JodaTimeAndroid.init(getApplicationContext());
    }

    // From https://developers.google.com/web/tools/chrome-devtools/remote-debugging/webviews
    // This enables all WebViews to be inspected by chrome on debuggable builds.
    private void enableWebDebugging() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }
    }

    private void setupMnemonicCode() {
        BitcoinUtils.init(getApplicationContext());
    }
}

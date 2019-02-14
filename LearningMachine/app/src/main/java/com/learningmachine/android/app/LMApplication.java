package com.learningmachine.android.app;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import java.util.Locale;

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
        setupBugsee();
        Timber.i("Application was launched!");
        logDeviceInfo();
    }

    private void setupBugsee() {
        if (BuildConfig.BUILD_TYPE.equals("qa")) {
            Bugsee.launch(this, BuildConfig.BUGSEE_KEY);
        }
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

    private void logDeviceInfo() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        String device = Build.DEVICE;
        String display = Build.DISPLAY;
        String fingerprint = Build.FINGERPRINT;
        String hardware = Build.HARDWARE;
        String host = Build.HOST;
        String id = Build.ID;
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String product = Build.PRODUCT;
        int sdk = Build.VERSION.SDK_INT;
        String codename = Build.VERSION.CODENAME;
        String release = Build.VERSION.RELEASE;
        String versionName = BuildConfig.VERSION_NAME;
        int versionCode = BuildConfig.VERSION_CODE;
        String displayCountry = Locale.getDefault().getDisplayCountry();
        String displayLanguage = Locale.getDefault().getDisplayLanguage();
        Timber.d(String.format(Locale.US, "Device Information:\n" +
                "device: %s\n" +
                "display: %s\n" +
                "fingerprint: %s\n" +
                "hardware: %s\n" +
                "host: %s\n" +
                "id: %s\n" +
                "manufacturer: %s\n" +
                "model: %s\n" +
                "product: %s\n" +
                "sdk: %d\n" +
                "codename: %s\n" +
                "release: %s\n" +
                "app version name: %s\n" +
                "app version code: %d\n" +
                "wifi: %s\n" +
                "mobile: %s\n" +
                "country: %s\n" +
                "language %s\n", device, display, fingerprint, hardware, host, id,
                model, manufacturer, product, sdk, codename, release, versionName, versionCode,
                wifiNetwork, mobileNetwork, displayCountry, displayLanguage));
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

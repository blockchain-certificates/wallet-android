package com.learningmachine.android.app.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.learningmachine.android.app.ui.home.HomeActivity;
import com.learningmachine.android.app.ui.issuer.IssuerActivity;
import com.learningmachine.android.app.ui.onboarding.OnboardingActivity;
import com.learningmachine.android.app.ui.onboarding.OnboardingFragment;
import com.learningmachine.android.app.ui.settings.SettingsActivity;
import com.learningmachine.android.app.util.AESCrypt;
import com.smallplanet.labalib.Laba;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.android.RxLifecycleAndroid;

import java.io.File;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.annotation.Nonnull;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

public abstract class LMActivity extends AppCompatActivity implements LifecycleProvider<ActivityEvent> {

    protected static Class lastImportantClassSeen = HomeActivity.class;

    // Used by LifecycleProvider interface to transform lifeycycle events into a stream of events through an observable.
    private final BehaviorSubject<ActivityEvent> mLifecycleSubject = BehaviorSubject.create();
    private Observable.Transformer mMainThreadTransformer;


    public void safeGoBack() {

        // ideallt what we want to do here is to safely go back to a known good activity in our flow.
        // for example, if we enter the app at home, go to settings, add a cert, go to cert info,
        // and delete the cert, where should we go back to?  Ideally that would be settings.
        //
        // however, if we do the same thing but go to issuers, then the cert, then going
        // back should go to the issuer activity...

        Intent intent = new Intent(this, lastImportantClassSeen);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLifecycleSubject.onNext(ActivityEvent.CREATE);
        Laba.setContext(getBaseContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLifecycleSubject.onNext(ActivityEvent.START);
        /*
         Toolbar in CertificatePagerActivity isn't being created properly because of a timing issue in the onCreate of LMActivity.
         CertificatePagerActivity is subclassing LMActivity and getSupportActionBar in setupActionBar is coming up null and not setting the proper toolbar
         so moving it to onStart sets the proper toolbar.
         */
        setupActionBar();

        Class c = this.getClass();
        if(c == HomeActivity.class || c == IssuerActivity.class || c == SettingsActivity.class) {
            lastImportantClassSeen = c;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLifecycleSubject.onNext(ActivityEvent.RESUME);


        if(didReceivePermissionsCallback){
            if(tempPassphrase != null && passphraseCallback != null) {
                if (didSucceedInPermissionsRequest) {
                    savePassphraseToDevice(tempPassphrase, passphraseCallback);
                } else {
                    savePassphraseToDevice(null, passphraseCallback);
                }
                tempPassphrase = null;
                passphraseCallback = null;
            }

            if(passphraseCallback != null) {
                if(didSucceedInPermissionsRequest){
                    getSavedPassphraseFromDevice(passphraseCallback);
                } else {
                    getSavedPassphraseFromDevice(passphraseCallback);
                }
                passphraseCallback = null;
            }

            didReceivePermissionsCallback = false;
            didSucceedInPermissionsRequest = false;
        }
    }

    @Override
    protected void onPause() {
        mLifecycleSubject.onNext(ActivityEvent.PAUSE);
        super.onPause();
    }

    @Override
    protected void onStop() {
        mLifecycleSubject.onNext(ActivityEvent.STOP);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mLifecycleSubject.onNext(ActivityEvent.DESTROY);
        super.onDestroy();
    }

    @Nonnull
    @Override
    public Observable<ActivityEvent> lifecycle() {
        return mLifecycleSubject.asObservable();
    }

    @Nonnull
    @Override
    public <T> LifecycleTransformer<T> bindUntilEvent(@Nonnull ActivityEvent event) {
        return RxLifecycle.bindUntilEvent(mLifecycleSubject, event);
    }

    @Nonnull
    @Override
    public <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindActivity(mLifecycleSubject);
    }

    /**
     * Used to compose an observable so that it observes results on the main thread and binds until activity Destruction
     */
    @SuppressWarnings("unchecked")
    protected <T> Observable.Transformer<T, T> bindToMainThread() {

        if (mMainThreadTransformer == null) {
            mMainThreadTransformer = (Observable.Transformer<T, T>) observable -> observable.observeOn(AndroidSchedulers.mainThread())
                    .compose(bindUntilEvent(ActivityEvent.DESTROY));
        }

        return (Observable.Transformer<T, T>) mMainThreadTransformer;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* Keyboard */

    public void hideKeyboard() {
        if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /* ActionBar */

    protected void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }

        actionBar.setDisplayShowTitleEnabled(true);
        String title = getActionBarTitle();
        if (!TextUtils.isEmpty(title)) {
            actionBar.setTitle(title);
        }

        // decide to display home caret
        if (requiresBackNavigation()) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public String getActionBarTitle() {
        return (String) getTitle();
    }

    /* Navigation */

    protected boolean requiresBackNavigation() {
        return false;
    }






    /* Saving passphrases to device */

    @FunctionalInterface
    public interface Callback <A, R> {
        public R apply (A a);
    }

    private String tempPassphrase = null;
    private Callback passphraseCallback = null;


    public static String pathToSavedPassphraseFile() {
        return Environment.getExternalStorageDirectory() + "/learningmachine.dat";
    }

    private String getDeviceId(Context context) {
        final String deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        if(deviceId == null || deviceId.length() == 0) {
            return "NOT_IDEAL_KEY";
        }
        return deviceId;
    }

    private void savePassphraseToDevice(String passphrase, Callback passphraseCallback) {
        if (passphrase == null) {
            passphraseCallback.apply(null);
            return;
        }

        String passphraseFile = pathToSavedPassphraseFile();
        try( PrintWriter out = new PrintWriter(passphraseFile) ) {
            String encryptionKey= getDeviceId(getApplicationContext());
            String mneumonicString = "mneumonic:"+passphrase;
            try {
                String encryptedMsg = AESCrypt.encrypt(encryptionKey, mneumonicString);
                out.println(encryptedMsg);
                passphraseCallback.apply(passphrase);
            }catch (GeneralSecurityException e){
                Timber.e(e, "Could not encrypt passphrase.");
                passphraseCallback.apply(null);
            }
        } catch(Exception e) {
            Timber.e(e, "Could not write to passphrase file");
            passphraseCallback.apply(null);
        }
    }

    public void askToSavePassphraseToDevice(String passphrase, Callback passphraseCallback) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                savePassphraseToDevice(passphrase, passphraseCallback);
            } else {
                tempPassphrase = passphrase;
                this.passphraseCallback = passphraseCallback;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        } else {
            savePassphraseToDevice(passphrase, passphraseCallback);
        }
    }

    public void askToGetPassphraseFromDevice(Callback passphraseCallback) {

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getSavedPassphraseFromDevice(passphraseCallback);
            } else {
                this.passphraseCallback = passphraseCallback;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        } else {
            getSavedPassphraseFromDevice(passphraseCallback);
        }
    }

    private boolean getSavedPassphraseFromDevice(Callback passphraseCallback) {
        String passphraseFile = OnboardingActivity.pathToSavedPassphraseFile();
        try {
            String encryptedMsg = new Scanner(new File(passphraseFile)).useDelimiter("\\Z").next();
            String encryptionKey = getDeviceId(getApplicationContext());
            try {
                String content = AESCrypt.decrypt(encryptionKey, encryptedMsg);
                if (content.startsWith("mneumonic:")) {
                    passphraseCallback.apply(content.substring(10).trim());
                    return true;
                }
            }catch (GeneralSecurityException e){
                Timber.e(e, "Could not decrypt passphrase.");
            }
        } catch(Exception e) {
            // note: this is a non-critical feature, so if this fails nbd
        }

        passphraseCallback.apply(null);
        return false;
    }



    private boolean didReceivePermissionsCallback = false;
    private boolean didSucceedInPermissionsRequest = false;
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Note: this really sucks, but android will crash if we try and display dialogs in the permissions
        // result callback.  So we delay this until onResume is called on the activity
        didReceivePermissionsCallback = true;
        didSucceedInPermissionsRequest = grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

}

package com.learningmachine.android.app.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.learningmachine.android.app.data.CertificateManager;
import com.learningmachine.android.app.data.passphrase.PassphraseManager;
import com.learningmachine.android.app.ui.home.HomeActivity;
import com.learningmachine.android.app.ui.issuer.IssuerActivity;
import com.learningmachine.android.app.ui.settings.SettingsActivity;
import com.learningmachine.android.app.util.AESCrypt;
import com.smallplanet.labalib.Laba;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.android.RxLifecycleAndroid;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.util.Scanner;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

public abstract class LMActivity extends AppCompatActivity implements LifecycleProvider<ActivityEvent> {
    protected static Class lastImportantClassSeen = HomeActivity.class;

    // Used by LifecycleProvider interface to transform lifeycycle events into a stream of events through an observable.
    private final BehaviorSubject<ActivityEvent> mLifecycleSubject = BehaviorSubject.create();
    private Observable.Transformer mMainThreadTransformer;

    @Inject PassphraseManager mPassphraseManager;

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
                    mPassphraseManager.savePassphraseToDevice(tempPassphrase, passphraseCallback);
                } else {
                    mPassphraseManager.savePassphraseToDevice(null, passphraseCallback);
                }
                tempPassphrase = null;
                passphraseCallback = null;
            }

            if(passphraseCallback != null) {
                mPassphraseManager.getSavedPassphraseFromDevice(passphraseCallback);
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

    private String tempPassphrase = null;
    private PassphraseManager.Callback<String, Boolean> passphraseCallback = null;

    public void askToSavePassphraseToDevice(String passphrase, PassphraseManager.Callback<String, Boolean> passphraseCallback) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                mPassphraseManager.savePassphraseToDevice(passphrase, passphraseCallback);
            } else {
                tempPassphrase = passphrase;
                this.passphraseCallback = passphraseCallback;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        } else {
            mPassphraseManager.savePassphraseToDevice(passphrase, passphraseCallback);
        }
    }

    public void askToGetPassphraseFromDevice(PassphraseManager.Callback<String, Boolean> passphraseCallback) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                mPassphraseManager.getSavedPassphraseFromDevice(passphraseCallback);
            } else {
                this.passphraseCallback = passphraseCallback;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        } else {
            mPassphraseManager.getSavedPassphraseFromDevice(passphraseCallback);
        }
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

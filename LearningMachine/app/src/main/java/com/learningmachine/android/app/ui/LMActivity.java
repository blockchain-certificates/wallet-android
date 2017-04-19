package com.learningmachine.android.app.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;

import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.android.RxLifecycleAndroid;

import javax.annotation.Nonnull;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.BehaviorSubject;

public abstract class LMActivity extends AppCompatActivity implements LifecycleProvider<ActivityEvent> {

    // Used by LifecycleProvider interface to transform lifeycycle events into a stream of events through an observable.
    private final BehaviorSubject<ActivityEvent> mLifecycleSubject = BehaviorSubject.create();
    private Observable.Transformer mMainThreadTransformer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLifecycleSubject.onNext(ActivityEvent.CREATE);
        setupActionBar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLifecycleSubject.onNext(ActivityEvent.START);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLifecycleSubject.onNext(ActivityEvent.RESUME);
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
                navigateBack();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
            title = getActionBarTitle();
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

    protected void navigateBack() {
        // do nothing
    }
}

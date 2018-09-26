package com.learningmachine.android.app.ui;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.learningmachine.android.app.BuildConfig;
import com.learningmachine.android.app.LMConstants;
import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.data.webservice.VersionService;
import com.learningmachine.android.app.util.DialogUtils;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.android.RxLifecycleAndroid;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

public class LMFragment extends Fragment implements LifecycleProvider<FragmentEvent> {

    // Used by LifecycleProvider interface to transform lifeycycle events into a stream of events through an observable.
    private final BehaviorSubject<FragmentEvent> mLifecycleSubject = BehaviorSubject.create();
    private Observable.Transformer mMainThreadTransformer;
    private AlertDialog mProgressDialog;

    @Inject
    protected VersionService mVersionService;

    protected interface OnVersionChecked {
        void needsUpdate(boolean updateNeeded);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mLifecycleSubject.onNext(FragmentEvent.ATTACH);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLifecycleSubject.onNext(FragmentEvent.CREATE);
        Timber.d("onCreate: " + getFileExtension(this.getClass().toString()));
        Injector.obtain(getContext())
                .inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLifecycleSubject.onNext(FragmentEvent.CREATE_VIEW);
    }

    @Override
    public void onStart() {
        super.onStart();
        mLifecycleSubject.onNext(FragmentEvent.START);
        Timber.d("onStart: " + getFileExtension(this.getClass().toString()));
    }

    @Override
    public void onResume() {
        super.onResume();
        mLifecycleSubject.onNext(FragmentEvent.RESUME);
        Timber.d("onResume: " + getFileExtension(this.getClass().toString()));
    }

    @Override
    public void onPause() {
        mLifecycleSubject.onNext(FragmentEvent.PAUSE);
        super.onPause();
        Timber.d("onPause: " + getFileExtension(this.getClass().toString()));
    }

    @Override
    public void onStop() {
        mLifecycleSubject.onNext(FragmentEvent.STOP);
        super.onStop();
        Timber.d("onStop: " + getFileExtension(this.getClass().toString()));
    }

    @Override
    public void onDestroyView() {
        mLifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mLifecycleSubject.onNext(FragmentEvent.DESTROY);
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        mLifecycleSubject.onNext(FragmentEvent.DETACH);
        super.onDetach();
    }

    @Override
    public final Observable<FragmentEvent> lifecycle() {
        return mLifecycleSubject.asObservable();
    }

    @Override
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull FragmentEvent event) {
        return RxLifecycle.bindUntilEvent(mLifecycleSubject, event);
    }

    @Override
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindFragment(mLifecycleSubject);
    }

    /**
     * Used to compose an observable so that it observes results on the main thread and binds until fragment Destruction
     */
    @SuppressWarnings("unchecked")
    protected <T> Observable.Transformer<T, T> bindToMainThread() {

        if (mMainThreadTransformer == null) {
            mMainThreadTransformer = (Observable.Transformer<T, T>) observable -> observable.observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .compose(bindUntilEvent(FragmentEvent.DESTROY));
        }

        return (Observable.Transformer<T, T>) mMainThreadTransformer;
    }


    protected void displayErrors(Throwable throwable, DialogUtils.ErrorCategory errorCategory, @StringRes int errorTitleResId) {
        hideProgressDialog();
        DialogUtils.showErrorAlertDialog(getContext(), getFragmentManager(), errorTitleResId, throwable, errorCategory);
    }

    protected void displayErrors(int errorID, Throwable throwable, DialogUtils.ErrorCategory errorCategory, @StringRes int errorTitleResId) {
        hideProgressDialog();
        DialogUtils.showErrorAlertDialog(getContext(), getFragmentManager(), errorTitleResId, errorID, throwable, errorCategory);
    }

    protected void displayProgressDialog(@StringRes int progressMessageResId) {
        FragmentActivity parentActivity = getActivity();
        if (parentActivity == null) {
            return;
        }
        getActivity().runOnUiThread(() -> mProgressDialog = DialogUtils.showProgressDialog(getContext(), getString(progressMessageResId)));

    }

    protected void hideProgressDialog() {
        if (mProgressDialog != null) {
            FragmentActivity parentActivity = getActivity();
            if (parentActivity == null) {
                return;
            }
            getActivity().runOnUiThread(() -> mProgressDialog.dismiss());
        }
    }

    protected void checkVersion(OnVersionChecked onVersionChecked) {
        if (mVersionService == null) {
            if (onVersionChecked != null) {
                onVersionChecked.needsUpdate(false);
            }
            return;
        }
        mVersionService.getVersion()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(version -> {
                    String playStoreVersion = version.android;
                    String localVersion = BuildConfig.VERSION_NAME;
                    localVersion = localVersion.split("-")[0];

                    String[] playStoreParts = playStoreVersion.split("\\.");
                    String[] localParts = localVersion.split("\\.");

                    if (playStoreParts.length != localParts.length) {
                        onVersionChecked.needsUpdate(false);
                        return;
                    }

                    boolean needsUpdate = false;
                    for (int i = 0; i < playStoreParts.length; i++) {
                        int psv = Integer.parseInt(playStoreParts[i]);
                        int lv = Integer.parseInt(localParts[i]);
                        if (psv == lv) {
                            continue;
                        }
                        if (psv < lv) {
                            break;
                        }
                        needsUpdate = true;
                    }
                    onVersionChecked.needsUpdate(needsUpdate);
                    if (needsUpdate) {
                        showVersionDialog();
                    }
                }, throwable -> onVersionChecked.needsUpdate(false));
    }

    private void showVersionDialog() {
        DialogUtils.showAlertDialog(getContext(), this,
                R.drawable.ic_dialog_warning,
                getResources().getString(R.string.check_version_title),
                getResources().getString(R.string.check_version_message),
                getResources().getString(R.string.ok_button),
                getResources().getString(R.string.check_version_message_cancel_title),
                (btnIdx) -> {
                    if((int)btnIdx == 1) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(LMConstants.PLAY_STORE_URL)));
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(LMConstants.PLAY_STORE_EXTERNAL_URL)));
                        }
                    } else {
                        Timber.i("User is not going to play store to update");
                    }
                    return null;
                });
    }

    // Snackbars

    protected void showSnackbar(View view, int messageResId) {
        Snackbar.make(view, messageResId, Snackbar.LENGTH_LONG)
                .show();
    }

    // Keyboard

    protected void hideKeyboard() {
        LMActivity activity = (LMActivity) getActivity();
        activity.hideKeyboard();
    }

    public static String getFileExtension(String name) {
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }
}

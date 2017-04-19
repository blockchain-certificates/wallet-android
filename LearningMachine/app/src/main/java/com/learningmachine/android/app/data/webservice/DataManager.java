package com.learningmachine.android.app.data.webservice;


import com.learningmachine.android.app.data.web.Issuer;

import retrofit2.Retrofit;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.schedulers.Schedulers;
import timber.log.Timber;


public class DataManager {
    private Retrofit mRetrofit;
    private Scheduler mSubscribeOnScheduler;
    private Scheduler mObserveOnScheduler;


    public DataManager(Retrofit retrofit) {
        mRetrofit = retrofit;
        mObserveOnScheduler = getObserveOnScheduler();
        mSubscribeOnScheduler = getSubscribeOnScheduler();
    }

    private Scheduler getObserveOnScheduler() {
        return Schedulers.io();
    }

    private Scheduler getSubscribeOnScheduler() {
        return Schedulers.io();
    }

    public void addIssuerRequest(String url) {
        IssuerService issuerService = mRetrofit.create(IssuerService.class);
        Observable<Issuer> getSample = issuerService.introductionRequest(url);
        getSample.subscribeOn(mSubscribeOnScheduler)
                .observeOn(mObserveOnScheduler)
                .subscribe(new Observer<Issuer>() {
                    @Override
                    public void onCompleted() {
                        Timber.d("Success");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Timber.e("Failed Call", throwable);
                    }

                    @Override
                    public void onNext(Issuer issuer) {
                        Timber.d("Results: email=%s, id=%s", issuer.getEmail(), issuer.getId());
                    }
                });
    }
}

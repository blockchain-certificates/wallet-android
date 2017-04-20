package com.learningmachine.android.app.data.webservice;


import com.learningmachine.android.app.data.web.Issuer;

import retrofit2.Retrofit;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;


public class DataManager {
    private Retrofit mRetrofit;
    private Scheduler mSubscribeOnScheduler;
    private Scheduler mObserveOnScheduler;
    private final IssuerService mIssuerService;

    public DataManager(Retrofit retrofit) {
        mRetrofit = retrofit;
        mObserveOnScheduler = getObserveOnScheduler();
        mSubscribeOnScheduler = getSubscribeOnScheduler();
        mIssuerService = mRetrofit.create(IssuerService.class);
    }

    private Scheduler getObserveOnScheduler() {
        return Schedulers.io();
    }

    private Scheduler getSubscribeOnScheduler() {
        return Schedulers.io();
    }

    public Observable<Issuer> addIssuerRequest(String url, String nonce) {
        return mIssuerService.getIssuer(url)
                .flatMap(issuer -> Observable.combineLatest(Observable.just(issuer),
                        mIssuerService.doIntroduction(issuer.getIntroductionURL(), nonce),
                        (issuer1, aVoid) -> issuer1))
                .observeOn(mObserveOnScheduler)
                .subscribeOn(mSubscribeOnScheduler);
    }

}

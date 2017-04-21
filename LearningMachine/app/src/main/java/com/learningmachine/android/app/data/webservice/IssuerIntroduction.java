package com.learningmachine.android.app.data.webservice;


import com.learningmachine.android.app.data.webservice.response.IssuerResponse;

import retrofit2.Retrofit;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;


public class IssuerIntroduction {
    private Scheduler mSubscribeOnScheduler;
    private Scheduler mObserveOnScheduler;
    private final IssuerService mIssuerService;

    public IssuerIntroduction(Retrofit retrofit) {
        mObserveOnScheduler = getObserveOnScheduler();
        mSubscribeOnScheduler = getSubscribeOnScheduler();
        mIssuerService = retrofit.create(IssuerService.class);
    }

    private Scheduler getObserveOnScheduler() {
        return Schedulers.io();
    }

    private Scheduler getSubscribeOnScheduler() {
        return Schedulers.io();
    }

    public Observable<IssuerResponse> addIssuer(String url, String nonce) {
        return mIssuerService.getIssuer(url)
                .flatMap(issuer -> Observable.combineLatest(Observable.just(issuer),
                        mIssuerService.doIntroduction(issuer.getIntroUrl(), nonce),
                        (issuer1, aVoid) -> issuer1))
                .observeOn(mObserveOnScheduler)
                .subscribeOn(mSubscribeOnScheduler);
    }

}

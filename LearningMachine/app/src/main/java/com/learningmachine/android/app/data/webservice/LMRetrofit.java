package com.learningmachine.android.app.data.webservice;


import com.learningmachine.android.app.data.web.AddIssuerRequest;

import retrofit2.Retrofit;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.schedulers.Schedulers;
import timber.log.Timber;


public class LMRetrofit {
    private Retrofit mRetrofit;
    private Scheduler mSubscribeOnScheduler;
    private Scheduler mObserveOnScheduler;


    public LMRetrofit(Retrofit retrofit) {
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
        Observable<AddIssuerRequest> getSample = issuerService.introductionRequest(url);
        getSample.subscribeOn(mSubscribeOnScheduler)
                .observeOn(mObserveOnScheduler)
                .subscribe(new Observer<AddIssuerRequest>() {
                    @Override
                    public void onCompleted() {
                        Timber.d("Success");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Timber.e("Failed Call", throwable);
                    }

                    @Override
                    public void onNext(AddIssuerRequest addIssuerRequest) {
                        Timber.d("Results: email=%s, id=%s", addIssuerRequest.getEmail(), addIssuerRequest.getId());
                    }
                });
    }
}

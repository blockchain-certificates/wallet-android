package com.learningmachine.android.app.data.webservice;


import com.learningmachine.android.app.data.webservice.request.IssuerIntroductionPayload;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;

import retrofit2.Retrofit;
import rx.Observable;
import rx.schedulers.Schedulers;


public class IssuerIntroduction {
    private final IssuerService mIssuerService;

    public IssuerIntroduction(Retrofit retrofit) {
        mIssuerService = retrofit.create(IssuerService.class);
    }

    public Observable<IssuerResponse> addIssuer(String url, String bitcoinAddress, String nonce) {
        IssuerIntroductionPayload payload = new IssuerIntroductionPayload("", nonce);
        return mIssuerService.getIssuer(url)
                .flatMap(issuer -> {
                    return Observable.combineLatest(Observable.just(issuer),
                            mIssuerService.doIntroduction(issuer.getIntroUrl(), payload),
                            (issuer1, aVoid) -> issuer1);
                })
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io());
    }
}

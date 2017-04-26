package com.learningmachine.android.app.data.webservice;


import com.learningmachine.android.app.data.webservice.request.IssuerIntroductionRequest;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;

import retrofit2.Retrofit;
import rx.Observable;


public class IssuerIntroduction {
    private final IssuerService mIssuerService;

    public IssuerIntroduction(Retrofit retrofit) {
        mIssuerService = retrofit.create(IssuerService.class);
    }

    public Observable<IssuerResponse> addIssuer(String url, String bitcoinAddress, String nonce) {
        IssuerIntroductionRequest request = new IssuerIntroductionRequest("", nonce);
        return mIssuerService.getIssuer(url)
                .flatMap(issuer -> {
                    return Observable.combineLatest(Observable.just(issuer),
                            mIssuerService.postIntroduction(issuer.getIntroUrl(), request),
                            (issuer1, aVoid) -> issuer1);
                });
    }
}

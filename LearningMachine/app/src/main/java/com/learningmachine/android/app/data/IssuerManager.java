package com.learningmachine.android.app.data;

import com.learningmachine.android.app.data.model.Issuer;
import com.learningmachine.android.app.data.store.IssuerStore;
import com.learningmachine.android.app.data.webservice.IssuerService;
import com.learningmachine.android.app.data.webservice.request.IssuerIntroductionRequest;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

public class IssuerManager {

    private IssuerStore mIssuerStore;
    private IssuerService mIssuerService;


    public IssuerManager(IssuerStore issuerStore, IssuerService issuerService) {
        mIssuerStore = issuerStore;
        mIssuerService = issuerService;
    }

    public Observable<List<Issuer>> getIssuers() {
        return Observable.just(mIssuerStore.loadIssuers());
    }

    public Observable<Void> addIssuer(String url, String bitcoinAddress, String nonce) {
        IssuerIntroductionRequest request = new IssuerIntroductionRequest(bitcoinAddress, nonce);
        return mIssuerService.getIssuer(url)
                .flatMap(issuer -> Observable.combineLatest(Observable.just(issuer),
                        mIssuerService.postIntroduction(issuer.getIntroUrl(), request),
                        (issuer1, aVoid) -> issuer1))
                .map(issuerResponse -> {
                    mIssuerStore.saveIssuerResponse(issuerResponse);
                    return null;
                });
    }
}

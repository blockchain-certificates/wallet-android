package com.learningmachine.android.app.data;

import android.content.Context;

import com.learningmachine.android.app.data.model.Issuer;
import com.learningmachine.android.app.data.store.IssuerStore;
import com.learningmachine.android.app.data.webservice.IssuerService;
import com.learningmachine.android.app.data.webservice.request.IssuerIntroductionRequest;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;
import com.learningmachine.android.app.util.GsonUtil;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import rx.Observable;
import timber.log.Timber;

public class IssuerManager {

    private IssuerStore mIssuerStore;
    private IssuerService mIssuerService;

    public IssuerManager(IssuerStore issuerStore, IssuerService issuerService) {
        mIssuerStore = issuerStore;
        mIssuerService = issuerService;
    }

    public Observable<Void> loadSampleIssuer(Context context) {
        try {
            GsonUtil gsonUtil = new GsonUtil(context);
            IssuerResponse issuerResponse = gsonUtil.loadModelObject("sample-issuer", IssuerResponse.class);
            mIssuerStore.saveIssuerResponse(issuerResponse);
            return Observable.just(null);
        } catch (IOException e) {
            Timber.e(e, "Unable to load Sample Issuer");
            return Observable.error(e);
        }
    }

    public Observable<Issuer> getIssuer(String issuerUuid) {
        return Observable.just(mIssuerStore.loadIssuer(issuerUuid));
    }

    public Observable<Issuer> getIssuerForCertificate(String certUuid) {
        return Observable.just(mIssuerStore.loadIssuerForCertificate(certUuid));
    }

    public Observable<List<Issuer>> getIssuers() {
        return Observable.just(mIssuerStore.loadIssuers());
    }

    public Observable<String> addIssuer(String url, String bitcoinAddress, String nonce) {
        IssuerIntroductionRequest request = new IssuerIntroductionRequest(bitcoinAddress, nonce);
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            Timber.e(e, "Failed to convert url string to URI");
            return Observable.error(e);
        }
        return mIssuerService.getIssuer(uri)
                .flatMap(issuer -> Observable.combineLatest(Observable.just(issuer),
                        mIssuerService.postIntroduction(issuer.getIntroUrl(), request),
                        (issuer1, aVoid) -> issuer1))
                .map(issuerResponse -> {
                    mIssuerStore.saveIssuerResponse(issuerResponse);
                    return issuerResponse.getUuid();
                });
    }
}

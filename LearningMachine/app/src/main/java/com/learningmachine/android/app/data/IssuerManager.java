package com.learningmachine.android.app.data;

import android.content.Context;

import com.learningmachine.android.app.data.model.Issuer;
import com.learningmachine.android.app.data.store.IssuerStore;
import com.learningmachine.android.app.data.webservice.IssuerService;
import com.learningmachine.android.app.data.webservice.request.IssuerIntroductionRequest;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;
import com.learningmachine.android.app.util.GsonUtil;

import java.io.IOException;
import java.util.List;

import rx.Observable;
import timber.log.Timber;

public class IssuerManager {

    private Context mContext;
    private IssuerStore mIssuerStore;
    private IssuerService mIssuerService;


    public IssuerManager(Context context, IssuerStore issuerStore, IssuerService issuerService) {
        mContext = context;
        mIssuerStore = issuerStore;
        mIssuerService = issuerService;
        loadSampleIssuer();
    }

    public Observable<Void> loadSampleIssuer() {
        GsonUtil gsonUtil = new GsonUtil(mContext);
        try {
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

    public void purgeIssuers() {
        mIssuerStore.reset();
        loadSampleIssuer();
    }
}

package com.learningmachine.android.app.data;

import com.learningmachine.android.app.data.model.Issuer;
import com.learningmachine.android.app.data.store.IssuerStore;

import java.util.List;

import rx.Observable;

public class IssuerManager {

    private IssuerStore mIssuerStore;

    public IssuerManager(IssuerStore issuerStore) {
        mIssuerStore = issuerStore;
    }

    public Observable<List<Issuer>> getIssuers() {
        return Observable.just(mIssuerStore.loadIssuers());
    }
}

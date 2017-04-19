package com.learningmachine.android.app.data.webservice;

import com.learningmachine.android.app.data.web.Issuer;

import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

public interface IssuerService {
    @GET
    Observable<Issuer> introductionRequest(@Url String url);
}

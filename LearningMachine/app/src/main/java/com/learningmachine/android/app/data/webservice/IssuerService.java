package com.learningmachine.android.app.data.webservice;

import com.learningmachine.android.app.data.web.AddIssuerRequest;

import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

public interface IssuerService {
    @GET
    Observable<AddIssuerRequest> introductionRequest(@Url String url);
}

package com.learningmachine.android.app.data.webservice;

import com.learningmachine.android.app.data.model.Version;

import retrofit2.http.GET;
import rx.Observable;

public interface VersionService {
    @GET("/versions.json")
    Observable<Version> getVersion();
}

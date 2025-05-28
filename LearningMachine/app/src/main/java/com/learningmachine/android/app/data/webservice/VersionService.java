package com.hyland.android.app.data.webservice;

import com.hyland.android.app.data.model.Version;

import retrofit2.http.GET;
import rx.Observable;

public interface VersionService {
    @GET("/versions.json")
    Observable<Version> getVersion();
}

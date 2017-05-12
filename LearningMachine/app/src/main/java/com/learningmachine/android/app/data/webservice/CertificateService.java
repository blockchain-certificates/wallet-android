package com.learningmachine.android.app.data.webservice;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

public interface CertificateService {

    @GET
    Observable<ResponseBody> getCertificate(@Url String url);
}

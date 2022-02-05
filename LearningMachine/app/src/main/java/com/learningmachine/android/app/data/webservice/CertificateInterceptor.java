package com.learningmachine.android.app.data.webservice;


import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

@Singleton
public class CertificateInterceptor implements Interceptor {

    @Inject
    public CertificateInterceptor() {}

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        HttpUrl url = request.url();

        HttpUrl jsonFormatAdded = url
                .newBuilder()
                .addEncodedQueryParameter("format", "json")
                .build();
        request = request.newBuilder()
                .url(jsonFormatAdded)
                .build();
        Timber.d(String.format("Performing Request: %s %s", request.method(), url.toString()));

        return chain.proceed(request);
    }
}

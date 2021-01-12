package com.learningmachine.android.app.data.webservice;


import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import timber.log.Timber;

public class CertificateInterceptor implements Interceptor {

    private static final String JSON_EXT = ".json";

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

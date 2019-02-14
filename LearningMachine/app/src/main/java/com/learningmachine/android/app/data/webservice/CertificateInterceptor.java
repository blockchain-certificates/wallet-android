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
        String urlString = url.toString();

        Timber.d(String.format("Performing Request: %s %s",
                request.method(), urlString));

        if (!urlString.endsWith(JSON_EXT)) {
            // first try to append json
            String urlStringAppended = urlString + JSON_EXT;
            HttpUrl urlAppended = HttpUrl.parse(urlStringAppended);
            request = request.newBuilder()
                    .url(urlAppended)
                    .build();
        }

        Response response = chain.proceed(request);
        Timber.d(String.format("response: %s", response.toString()));
        if (response.body() != null) {
            ResponseBody responseBody = response.body();
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.buffer();
            String responseBodyString = buffer.clone().readString(Charset.forName("UTF-8"));
            Timber.d(String.format("response body: %s", responseBodyString));
        }

        if (!response.isSuccessful()) {
            // use query params
            url = request.url();
            urlString = url.toString();
            String jsonExtRemoved = urlString.replace(JSON_EXT, "");
            HttpUrl jsonFormatAdded = HttpUrl.parse(jsonExtRemoved)
                    .newBuilder()
                    .addEncodedQueryParameter("format", "json")
                    .build();
            request = request.newBuilder()
                    .url(jsonFormatAdded)
                    .build();
        }

        return chain.proceed(request);
    }
}

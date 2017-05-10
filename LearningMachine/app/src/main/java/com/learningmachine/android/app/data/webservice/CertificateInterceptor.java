package com.learningmachine.android.app.data.webservice;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CertificateInterceptor implements Interceptor {

    private static final String JSON_EXT = ".json";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        HttpUrl url = request.url();
        String urlString = url.toString();

        if (!urlString.endsWith(JSON_EXT)) {
            // first try to append json
            String urlStringAppended = urlString + JSON_EXT;
            HttpUrl urlAppended = HttpUrl.parse(urlStringAppended);
            request = request.newBuilder()
                    .url(urlAppended)
                    .build();
        }

        Response response = chain.proceed(request);
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
            response = chain.proceed(request);
        }

        return response;
    }
}

package com.learningmachine.android.app.data.inject;

import com.learningmachine.android.app.LMConstants;
import com.learningmachine.android.app.data.webservice.CertificateService;
import com.learningmachine.android.app.data.webservice.IssuerService;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

@Module
public class ApiModule {

    @Provides
    @Singleton
    @Named("logging")
    Interceptor provideInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }

    @Provides
    @Singleton
    @Named("issuer")
    OkHttpClient provideIssuerOkHttpClient(@Named("logging") Interceptor loggingInterceptor) {
        return new OkHttpClient.Builder().addInterceptor(loggingInterceptor)
                .build();
    }

    @Provides
    @Singleton
    @Named("issuer")
    Retrofit provideIssuerRetrofit(@Named("issuer") OkHttpClient okHttpClient) {
        return new Retrofit.Builder().baseUrl(LMConstants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();
    }

    @Provides
    @Singleton
    IssuerService provideIssuerService(@Named("issuer") Retrofit retrofit) {
        return retrofit.create(IssuerService.class);
    }

    @Provides
    @Singleton
    @Named("certificate")
    Interceptor provideCertificateInterceptor() {
        return chain -> {
            Request request = chain.request();
            HttpUrl url = request.url();
            String urlString = url.toString();

            if (!urlString.contains(".json")) {
                // first try to append json
                String urlStringAppended = urlString + ".json";
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
                String jsonExtRemoved = urlString.replace(".json", "");
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
        };
    }

    @Provides
    @Singleton
    @Named("certificate")
    OkHttpClient provideCertificateOkHttpClient(@Named("logging") Interceptor loggingInterceptor, @Named("certificate") Interceptor certificateInterceptor) {
        return new OkHttpClient.Builder().addInterceptor(loggingInterceptor)
                .addInterceptor(certificateInterceptor)
                .build();
    }

    @Provides
    @Singleton
    @Named("certificate")
    Retrofit provideCertificateRetrofit(@Named("certificate") OkHttpClient okHttpClient) {
        return new Retrofit.Builder().baseUrl(LMConstants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();
    }

    @Provides
    @Singleton
    CertificateService provideCertificateService(@Named("certificate") Retrofit retrofit) {
        return retrofit.create(CertificateService.class);
    }
}

package com.learningmachine.android.app.data.inject;

import com.learningmachine.android.app.LMConstants;
import com.learningmachine.android.app.data.webservice.BlockchainService;
import com.learningmachine.android.app.data.webservice.CertificateInterceptor;
import com.learningmachine.android.app.data.webservice.CertificateService;
import com.learningmachine.android.app.data.webservice.IssuerService;
import com.learningmachine.android.app.data.webservice.VersionService;

import java.nio.charset.Charset;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@Module
public class ApiModule {

    @Provides
    @Singleton
    Interceptor provideLoggingInterceptor() {
        return chain -> {
            Request request = chain.request();

            Timber.d(String.format("Performing Request: %s %s",
                    request.method(), request.url().toString()));

            if (request.body() != null) {
                Buffer buffer = new Buffer();
                request.body().writeTo(buffer);
                String body = buffer.readUtf8();
                Timber.d(String.format("body: %s", body));
            }

            Response response = chain.proceed(request);
            Timber.d(String.format("response: %s", response.toString()));
            if (response.body() != null && !response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE);
                Buffer buffer = source.buffer();
                String responseBodyString = buffer.clone().readString(Charset.forName("UTF-8"));
                Timber.d(String.format("response body: %s", responseBodyString));
            }

            return response;
        };
    }

    @Provides
    @Singleton
    @Named("issuer")
    OkHttpClient provideIssuerOkHttpClient(Interceptor loggingInterceptor) {
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
    CertificateInterceptor provideCertificateInterceptor() {
        return new CertificateInterceptor();
    }

    @Provides
    @Singleton
    @Named("certificate")
    OkHttpClient provideCertificateOkHttpClient(Interceptor loggingInterceptor, CertificateInterceptor certificateInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
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

    @Provides
    @Singleton
    @Named("blockchain")
    Retrofit provideBlockchainRetrofit(@Named("issuer") OkHttpClient okHttpClient) {
        return new Retrofit.Builder().baseUrl(LMConstants.BLOCKCHAIN_SERVICE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();
    }

    @Provides
    @Singleton
    BlockchainService provideBlockchainService(@Named("blockchain") Retrofit retrofit) {
        return retrofit.create(BlockchainService.class);
    }

    @Provides
    @Singleton
    @Named("version")
    Retrofit provideVersionRetrofit(@Named("issuer") OkHttpClient okHttpClient) {
        return new Retrofit.Builder().baseUrl(LMConstants.VERSION_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();
    }

    @Provides
    @Singleton
    VersionService provideVersionService(@Named("version") Retrofit retrofit) {
        return retrofit.create(VersionService.class);
    }
}

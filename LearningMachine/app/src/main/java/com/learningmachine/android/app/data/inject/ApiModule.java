package com.learningmachine.android.app.data.inject;

import com.learningmachine.android.app.LMConstants;
import com.learningmachine.android.app.data.model.Certificate;
import com.learningmachine.android.app.data.webservice.CertificateInterceptor;
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
    Interceptor provideLoggingInterceptor() {
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
    CertificateInterceptor provideCertificateInterceptor() {
        return new CertificateInterceptor();
    }

    @Provides
    @Singleton
    @Named("certificate")
    OkHttpClient provideCertificateOkHttpClient(@Named("logging") Interceptor loggingInterceptor, CertificateInterceptor certificateInterceptor) {
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
}

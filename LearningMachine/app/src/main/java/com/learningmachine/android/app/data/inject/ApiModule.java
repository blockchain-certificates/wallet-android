package com.learningmachine.android.app.data.inject;

import com.learningmachine.android.app.LMConstants;
import com.learningmachine.android.app.data.webservice.BlockchainService;
import com.learningmachine.android.app.data.webservice.CertificateInterceptor;
import com.learningmachine.android.app.data.webservice.CertificateService;
import com.learningmachine.android.app.data.webservice.IssuerService;
import com.learningmachine.android.app.data.webservice.VersionService;

import junit.runner.Version;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

@Module
public class ApiModule {

    @Provides
    @Singleton
    Interceptor provideLoggingInterceptor(HttpLoggingInterceptor.Level level) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(level);
        return loggingInterceptor;
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

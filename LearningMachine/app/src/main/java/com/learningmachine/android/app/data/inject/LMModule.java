package com.learningmachine.android.app.data.inject;

import android.content.Context;

import com.learningmachine.android.app.LMConstants;
import com.learningmachine.android.app.data.webservice.LMRetrofit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class LMModule {

    private Context mApplicationContext;

    public LMModule(Context applicationContext) {
        mApplicationContext = applicationContext;
    }

    @Singleton
    @Provides
    Context providesContext() {
        return mApplicationContext;
    }

    @Singleton
    @Provides
    Interceptor provideInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }

    @Singleton
    @Provides
    OkHttpClient provideOkHttpClient(Interceptor loggingInterceptor) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
        return okHttpClient;
    }

    @Singleton
    @Provides Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(LMConstants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit;
    }

    @Singleton
    @Provides LMRetrofit provideLMRetrofit(Retrofit retrofit) {
        return new LMRetrofit(retrofit);
    }
}

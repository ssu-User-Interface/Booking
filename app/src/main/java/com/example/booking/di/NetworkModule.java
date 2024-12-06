package com.example.booking.di;

import com.example.booking.service.AladinApiService;
import com.example.booking.service.AladinDetailApiService;
import com.example.booking.service.NaverApiService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    private static final String BASE_URL = "https://openapi.naver.com/";
    private static final String ALADIN_BASE_URL = "https://www.aladin.co.kr/ttb/api/";

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(chain -> {
                    return chain.proceed(chain.request().newBuilder()
                            .addHeader("X-Naver-Client-Id", "ArlTo44hRSd3aCikV7EV")
                            .addHeader("X-Naver-Client-Secret", "pbvLP90Nuy")
                            .build());
                })
                .build();
    }

    @Provides
    @Singleton
    public Gson provideGson() {
        return new GsonBuilder()
                .setLenient()
                .create();
    }

    @Provides
    @Singleton
    @AladinRetrofit
    public Retrofit provideAladinRetrofit(Gson gson, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(ALADIN_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    @Provides
    @Singleton
    @NaverRetrofit
    public Retrofit provideRetrofit(OkHttpClient okHttpClient, Gson gson) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))  // 유연한 Gson 사용
                .build();
    }


    @Provides
    @Singleton
    public NaverApiService provideNaverService(@NaverRetrofit Retrofit retrofit) {
        return retrofit.create(NaverApiService.class);
    }


    @Provides
    @Singleton
    public AladinApiService provideAladinService(@AladinRetrofit Retrofit retrofit) {
        return retrofit.create(AladinApiService.class);
    }

    @Provides
    @Singleton
    public AladinDetailApiService provideAladinDetailService(@AladinRetrofit Retrofit retrofit) {
        return retrofit.create(AladinDetailApiService.class);
    }

}


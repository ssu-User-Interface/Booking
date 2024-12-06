package com.example.booking.di;

import com.example.booking.datasource.AladinDataSource;
import com.example.booking.datasource.NaverDataSource;
import com.example.booking.datasourceImpl.AladinDataSourceImpl;
import com.example.booking.datasourceImpl.NaverDataSourceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DataSourceModule {

    @Provides
    @Singleton
    public NaverDataSource provideNaverDataSource(NaverDataSourceImpl naverDataSourceImpl) {
        return naverDataSourceImpl;
    }

    @Provides
    @Singleton
    public AladinDataSource provideAladinDataSource(AladinDataSourceImpl aladinDataSourceImpl) {
        return aladinDataSourceImpl;
    }

}

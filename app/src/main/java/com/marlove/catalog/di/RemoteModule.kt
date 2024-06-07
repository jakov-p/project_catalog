package com.marlove.catalog.di

import com.marlove.catalog.data.repository.IRemoteCatalogSource
import com.marlove.catalog.data.source.remote.RemoteCatalogSource
import com.marlove.catalog.data.source.remote.retrofit.AccessToken
import com.marlove.catalog.data.source.remote.retrofit.CatalogServiceApi
import com.marlove.catalog.data.source.remote.retrofit.ICatalogServiceApi
import com.marlove.catalog.data.source.remote.retrofit.RetrofitClient
import com.marlove.catalog.data.source.remote.retrofit.ServerUrl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteModule {

    companion object {

        @ServerUrl
        @Provides
        @Singleton
        fun getServerUrl(): String = RetrofitClient.SERVER_URL


        @AccessToken
        @Provides
        @Singleton
        fun getAccessToken(): String = RetrofitClient.ACCESS_TOKEN
    }

    @Binds
    @Singleton
    abstract fun  catalogServiceApi(catalogServiceApi: CatalogServiceApi): ICatalogServiceApi


    @Binds
    @Singleton
    abstract fun  remoteCatalogSource(remoteCatalogSource: RemoteCatalogSource): IRemoteCatalogSource
}
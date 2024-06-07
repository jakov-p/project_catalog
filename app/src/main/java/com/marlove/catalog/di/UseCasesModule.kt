package com.marlove.catalog.di

import com.marlove.catalog.data.repository.CatalogRepository
import com.marlove.catalog.domain.repository.ICatalogRepository
import com.marlove.catalog.domain.usecase.GetCatalogItemDetailsUseCase
import com.marlove.catalog.domain.usecase.GetCatalogItemsUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class UseCasesModule {

    @Binds
    @Singleton
    abstract fun catalogRepository(catalogRepository: CatalogRepository): ICatalogRepository

    companion object {

        @Provides
        @Singleton
        fun provideCatalogItemDetailUseCase(catalogRepository: ICatalogRepository) =
            GetCatalogItemDetailsUseCase(catalogRepository)


        @Provides
        @Singleton
        fun provideGetCatalogItemsUseCase(catalogRepository: ICatalogRepository )=
            GetCatalogItemsUseCase(catalogRepository)
    }
}
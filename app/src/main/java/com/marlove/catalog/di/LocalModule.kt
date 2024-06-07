package com.marlove.catalog.di

import android.content.Context
import androidx.room.Room
import com.marlove.catalog.data.repository.ILocalCatalogSource
import com.marlove.catalog.data.source.local.LocalCatalogSource
import com.marlove.catalog.data.source.local.room.AppDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalModule {

    @Binds
    @Singleton
    abstract fun  localCatalogSource(localCatalogSource: LocalCatalogSource): ILocalCatalogSource


    companion object {
        @Singleton
        @Provides
        fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java, "app_database"
            ).build()
        }

    }

}
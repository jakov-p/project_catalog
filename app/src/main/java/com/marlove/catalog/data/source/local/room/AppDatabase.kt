package com.marlove.catalog.data.source.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [CatalogItemEntity::class],
          version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun catalogItemDao(): CatalogItemDao
}




package com.marlove.catalog.data.source.local

import com.marlove.catalog.data.repository.ILocalCatalogSource
import com.marlove.catalog.data.source.local.room.AppDatabase
import com.marlove.catalog.data.source.local.room.CatalogItemDao
import com.marlove.catalog.data.source.local.room.CatalogItemEntity
import com.marlove.catalog.domain.model.CatalogItem
import com.marlove.catalog.logger.log
import javax.inject.Inject

class LocalCatalogSource @Inject constructor( appDatabase: AppDatabase)
    : ILocalCatalogSource {

    private val catalogItemDao: CatalogItemDao = appDatabase.catalogItemDao()

    override fun getAllWithoutImage(): List<CatalogItem> =
        catalogItemDao.getAllWithoutImage().map{ it.toCatalogItem()}.
        also {
            log.d("Read $it items from dbase (without images)")
        }

    override fun insertWithoutImage(catalogItems: List<CatalogItem>):Unit =
        catalogItemDao.insertWithoutImage(catalogItems.map { CatalogItemEntity(it) }).
        also {
            log.d("Inserted ${catalogItems.size} items (without images) into dbase")
        }

    override fun getImage(id:String): ByteArray? =
        catalogItemDao.getImage(id).
        also {
            log.d("Read an image from dbase, item id = $id ")
        }

    override fun updateImage(id:String, imageBytes: ByteArray) {
        catalogItemDao.updateImage(id, imageBytes).
        also {
            log.d("Inserted an image into dbase, item id = $id ")
        }
    }

    override fun deleteAll() {
        catalogItemDao.deleteAll().
        also {
            log.d("Deleted all the dbase's contents ")
        }
    }
}
package com.marlove.catalog.data.source.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.marlove.catalog.data.source.local.room.CatalogItemEntity.Companion.CONFIDENCE
import com.marlove.catalog.data.source.local.room.CatalogItemEntity.Companion.ID
import com.marlove.catalog.data.source.local.room.CatalogItemEntity.Companion.IMAGE_BYTES
import com.marlove.catalog.data.source.local.room.CatalogItemEntity.Companion.IMAGE_URL
import com.marlove.catalog.data.source.local.room.CatalogItemEntity.Companion.TABLE_NAME
import com.marlove.catalog.data.source.local.room.CatalogItemEntity.Companion.TEXT

@Dao
interface CatalogItemDao {

    @Query("SELECT $ID, $TEXT, $CONFIDENCE, $IMAGE_URL  FROM  $TABLE_NAME")
    fun getAllWithoutImage(): List<CatalogItemEntity>

    @Insert
    fun insertWithoutImage(catalogItemEntities: List<CatalogItemEntity>)

    @Query("SELECT $IMAGE_BYTES FROM  $TABLE_NAME WHERE $ID = :id")
    fun getImage(id:String): ByteArray?

    @Query("UPDATE $TABLE_NAME SET  $IMAGE_BYTES = :imageBytes WHERE $ID = :id")
    fun updateImage(id:String,  imageBytes: ByteArray?)

    @Query("DELETE FROM $TABLE_NAME")
    fun deleteAll()
}

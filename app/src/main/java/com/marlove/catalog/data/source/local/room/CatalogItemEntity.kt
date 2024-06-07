package com.marlove.catalog.data.source.local.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.marlove.catalog.domain.model.CatalogItem
import com.marlove.catalog.domain.model.CatalogItemDetails


@Entity(tableName = CatalogItemEntity.TABLE_NAME)
data class CatalogItemEntity(

    @ColumnInfo(name = ID)
    @PrimaryKey val
    id:String,

    @ColumnInfo(name = TEXT)
    val text: String,

    @ColumnInfo(name = CONFIDENCE)
    val confidence: Float,

    @ColumnInfo(name = IMAGE_URL)
    val imageUrl:String,

    //this is optional since the image bytes are not always available
    @ColumnInfo(name = IMAGE_BYTES)
    val imageBytes: ByteArray?
)
{
    //when entering Catalog Items they do not have images
    constructor (catalogItem: CatalogItem):
            this(catalogItem.id, catalogItem.text, catalogItem.confidence, catalogItem.imageUrl,  null)

    companion object {
        const val TABLE_NAME = "catalog_item"
        const val ID = "id"
        const val TEXT = "text"
        const val CONFIDENCE = "confidence"
        const val IMAGE_URL = "image_url"
        const val IMAGE_BYTES = "image_bytes"
    }

    /**
     * Converters to the corresponding domain objects
     * The dbase entry without the image bytes is a CatalogItem,
     * while with the images bytes represents a CatalogItemDetails
     */
    fun toCatalogItem() = CatalogItem(id, text, confidence, imageUrl)

    fun toCatalogItemDetails() = CatalogItemDetails(id, text, confidence, imageUrl, imageBytes)
}





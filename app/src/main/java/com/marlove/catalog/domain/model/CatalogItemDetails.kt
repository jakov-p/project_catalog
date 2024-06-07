package com.marlove.catalog.domain.model

/**
 * A catalog item details
 *
 * Note that it contains image bytes field, that can be empty or filled.
 * The empty field means that the image's contents has not be fetched so far.
 */
data class CatalogItemDetails(
    val id:String,
    val text: String,
    val confidence: Float,
    val imageUrl:String,
    val imageBytes: ByteArray?) {

    constructor(catalogItem: CatalogItem, imageBytes: ByteArray?):
        this(catalogItem.id,
            catalogItem.text,
            catalogItem.confidence,
            catalogItem.imageUrl,
            imageBytes )

}


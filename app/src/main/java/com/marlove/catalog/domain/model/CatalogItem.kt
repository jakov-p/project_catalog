package com.marlove.catalog.domain.model

/**
 * A catalog item
 *
 * Note that it contains no image bytes, but only the image's url.
 */
data class CatalogItem(
    val id:String,
    val text: String,
    val confidence: Float,
    val imageUrl:String,
)

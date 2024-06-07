package com.marlove.catalog.data.source.remote.retrofit.model

import com.google.gson.annotations.SerializedName
import com.marlove.catalog.domain.model.CatalogItem

/**
 * Mapper for JSON returned in the HTTP response.
 */
data class CatalogItemDto(
    @SerializedName("_id") val id:String,
    val text: String,
    val confidence: Float,
    @SerializedName("image")  val imageUrl:String) {


    //Converter to the corresponding domain object
    fun toCatalogItem() = CatalogItem(id, text, confidence, imageUrl)
}

package com.marlove.catalog.data.source.remote.retrofit

import com.marlove.catalog.data.source.remote.retrofit.model.CatalogItemDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface  ICatalogServiceApi
{
    //example: https://marlove.net/e/mock/v1/items?since_id=664f410e6df05
    @GET("items?")
    suspend fun getItemsSinceID(@Query("since_id") sinceId: String?): Response<List<CatalogItemDto>>

    //example: https://marlove.net/e/mock/v1/items?max_id=664f410e6df05
    @GET("items?")
    suspend fun getItemsMaxId(@Query("max_id") maxId: String?): Response<List<CatalogItemDto>>
}
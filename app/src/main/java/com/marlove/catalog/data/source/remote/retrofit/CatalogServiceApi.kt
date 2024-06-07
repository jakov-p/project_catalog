package com.marlove.catalog.data.source.remote.retrofit

import com.marlove.catalog.data.source.remote.retrofit.model.CatalogItemDto
import retrofit2.Response
import javax.inject.Inject

class CatalogServiceApi @Inject constructor (val retrofitClient: RetrofitClient):
    ICatalogServiceApi {

    /**
     * Get the catalog items newer than the specified id
     * @param sinceId id as the lower boundary
     */
    override suspend fun getItemsSinceID(sinceId: String?): Response<List<CatalogItemDto>> =
       retrofitClient.service.getItemsSinceID(sinceId)

    /**
     * Get the catalog items older than the specified id
     * @param maxId id as the lower boundary
     */
    override suspend fun getItemsMaxId(maxId: String?): Response<List<CatalogItemDto>> =
        retrofitClient.service.getItemsMaxId(maxId)

}
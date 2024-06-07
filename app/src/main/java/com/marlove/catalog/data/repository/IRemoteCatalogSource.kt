package com.marlove.catalog.data.repository

import com.marlove.catalog.domain.model.CatalogItem

interface IRemoteCatalogSource {

    /**
     * Get the catalog items newer than the specified id
     * @param sinceId id as the lower boundary
     */
    suspend fun getItemsSinceID(sinceId: String?): List<CatalogItem>

    /**
     * Get the catalog items older than the specified id
     * @param maxId id as the lower boundary
     */
    suspend fun getItemsMaxId(maxId: String?): List<CatalogItem>
}
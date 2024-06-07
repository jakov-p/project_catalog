package com.marlove.catalog.domain.repository

import com.marlove.catalog.domain.model.CatalogItemDetails
import kotlinx.coroutines.flow.Flow

/**
 * Interface for fetching Catalog Item Details for a single
 * Catalog Item. It supports canceling, as well.
 *
 */
interface ICatalogItemDetailsProvider {

    /**
     * Gets Catalog Item Details for a single Catalog Item
     *
     * @returns Flow of the current states during the action of
     *           fetching Catalog Item Details.
     */
    fun getFlow(): Flow<ResultState<CatalogItemDetails>>

    /**
     * Stops the already started the action of fetching Catalog Item Details.
     */
    fun cancel()
}
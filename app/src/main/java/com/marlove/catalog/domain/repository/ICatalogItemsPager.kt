package com.marlove.catalog.domain.repository

import androidx.paging.PagingData
import com.marlove.catalog.domain.model.CatalogItem
import kotlinx.coroutines.flow.Flow

/**
 * This class unifies all the functionalities related to fetching
 * Catalog Items list.
 *
 * It provides paging of the incoming Catalog Items.
 * It also supports refreshing.
 *
 */
interface ICatalogItemsPager
{
    /**
     * returns a flow of paged CatalogItem data
     */
    fun getPagingDataFlow(): Flow<PagingData<CatalogItem>>

    /**
     * forces the refreshing of the CatalogItem data
     * @return the flow of the refreshing action states
     */
    fun refresh(): Flow<ResultState<Unit>>

    /**
     * shuts it down
     */
    suspend fun close()
}
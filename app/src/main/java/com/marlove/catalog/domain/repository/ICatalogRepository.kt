package com.marlove.catalog.domain.repository

/**
 * Interface for fetching the data
 * (entry into the Data layer)
 */
interface ICatalogRepository {

    /**
     * Returns the object representing "the stream of Catalog Items".
     *
     * @param pageSize the batches in which the CatalogItems will be returned
     * @return the object representing "the stream of Catalog Items"
     */
    fun getCatalogItems(pageSize:Int = 10): ICatalogItemsPager

    /**
     * Returns the details for a particular Catalog Item.
     *
     * @param id the unique identifier of a Catalog Item
     * @return the full data on this Catalog Item
     */
    fun getCatalogItemDetails(id:String): ICatalogItemDetailsProvider
}

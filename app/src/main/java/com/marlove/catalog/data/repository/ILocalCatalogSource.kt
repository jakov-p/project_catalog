package com.marlove.catalog.data.repository

import com.marlove.catalog.domain.model.CatalogItem

/**
 * Represents the local cache of the downloaded data.
 *
 * Note that catalog entries are entered without the image bytes, and then later the
 * image bytes (when fetched from the remote store)  are entered separately.
 *
 * Also when the data is read from the cache the catalog entries are read as a list
 * of catalog items without images, When a particular image is needed, it is read
 * then separately.
 *
 * This way image bytes do not stay in memory all the time, only the other fields.
 * That saves memory.
 */
interface ILocalCatalogSource {

    /**
     * Return all the catalog items currently in the table.
     * The image bytes are not returned.
     *
     * @return all the entries in the table
     */
    fun getAllWithoutImage(): List<CatalogItem>

    /**
     * Insert a catalog item (one or more) but without the image bytes
     *
     * @param catalogItems the items to be inserted
     */
    fun insertWithoutImage(catalogItems: List<CatalogItem>)

    /**
     * Get the image bytes  for this catalog item
     * @param id the catalog item's id
     * @return the raw bytes of the image (as downloaded from the site)
     */
    fun getImage(id:String): ByteArray?

    /**
     * Inserts the (downloaded) image bytes into the corresponding catalog item
     *
     * @param id the catalog item's id
     * @param imageBytes the raw bytes as downloaded from the site
     */
    fun updateImage(id:String, imageBytes: ByteArray)
}
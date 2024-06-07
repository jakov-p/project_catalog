package com.marlove.catalog.data.repository.list

import com.marlove.catalog.data.repository.ILocalCatalogSource
import com.marlove.catalog.data.repository.IRemoteCatalogSource
import com.marlove.catalog.domain.model.CatalogItem
import com.marlove.catalog.logger.log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Called by pager. Its purpose is to deliver the Catalog items in a given range.
 * It does it by combining the local and remote data storage.
 *
 * If the requested data is already in the local data storage, it will be read from there. If
 * not there, then fetcher goes to the remote storage. Every downloaded catalog item will be
 * stored locally for later use.
 *
 * @property allLocalCatalogItems
 * @property localCatalog
 * @property remoteCatalog
 * @property dispatcher
 */
class Fetcher(
    private val allLocalCatalogItems: MutableList<CatalogItem>,
    private val localCatalog: ILocalCatalogSource,
    private val remoteCatalog: IRemoteCatalogSource,
    private val dispatcher : CoroutineDispatcher
)
 {

    suspend fun getCatalogItems(fromIndex: Int, loadSize: Int): List<CatalogItem> =
        withContext(dispatcher) {

            log.i("******* The Pager requests $loadSize items")

            val extraItems = allLocalCatalogItems.size - (fromIndex + loadSize)
            if (extraItems < 0) {
                fetchNewCatalogItemsRemotely(-extraItems)
            }

            if (allLocalCatalogItems.size >= (fromIndex + loadSize)) {
                log.i("******* The Pager receives $loadSize items")
                allLocalCatalogItems.subList(fromIndex, fromIndex + loadSize).toList()
            } else {
                log.i("******* Not enough items to return to the Pager," +
                        " only ${allLocalCatalogItems.size - fromIndex} will be returned")
                allLocalCatalogItems.subList(fromIndex, allLocalCatalogItems.size).toList()
            }
        }


    private suspend fun fetchNewCatalogItemsRemotely( toFetchCount:Int): Int {

        val oldItemsSize = allLocalCatalogItems.size
        log.i("Trying to fetch $toFetchCount items from the remote storage")

        //it will be read in more batches because we can not control how many items
        // will be returned from the remote source
        while(true) {
            val remainingToFetch = toFetchCount - (allLocalCatalogItems.size - oldItemsSize)
            if(remainingToFetch > 0) {

                log.d("Still $remainingToFetch items to be fetched from the remote storage")

                val lastId = if (allLocalCatalogItems.isEmpty()) {
                    log.d("This is the first fetching from the remote storage")
                    null
                } else {
                    val foundLastId = allLocalCatalogItems.last().id
                    log.d("Fetching from the remote storage from id = $foundLastId")
                    foundLastId
                }

                val newCatalogItems = remoteCatalog.getItemsMaxId(lastId)
                if(newCatalogItems.isNotEmpty()) {
                    localCatalog.insertWithoutImage(newCatalogItems)
                    allLocalCatalogItems += newCatalogItems
                    log.d("In this batch ${newCatalogItems.size} items " +
                            "were fetched from the remote storage")
                }
                else {
                    log.d("The end is reached at the remote storage")
                    break
                }
            }
            else {
                log.d("No need to fetch from the remote storage any more")
                break
            }
        }

        return allLocalCatalogItems.size - oldItemsSize
    }
}


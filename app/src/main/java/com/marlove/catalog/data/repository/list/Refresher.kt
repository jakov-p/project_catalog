package com.marlove.catalog.data.repository.list

import com.marlove.catalog.data.repository.ILocalCatalogSource
import com.marlove.catalog.data.repository.IRemoteCatalogSource
import com.marlove.catalog.domain.model.CatalogItem
import com.marlove.catalog.logger.log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext


/**
 * Since the functionality on the site  does not ever return any new catalog items,
 * then refresh always ends us with empty list, which would then normally cause no
 * change in the local storage.
 *
 * But, just for show, we will pretend as if an empty list is a reason enough
 * to go through the code that changes the local cache.
 */
const val isPretendingRefreshReturnedSomething = true

class Refresher(private val allLocalCatalogItems: MutableList<CatalogItem>,
                private val localCatalogSource: ILocalCatalogSource,
                private val remoteCatalog: IRemoteCatalogSource,
                private val dispatcher : CoroutineDispatcher )
{
    suspend fun refresh( )  =  withContext(dispatcher) {

        val firstId = allLocalCatalogItems.first().id
        log.d("Fetching from the remote storage all newer than  id = $firstId")
        val newCatalogItems = remoteCatalog.getItemsSinceID(firstId)

        if(newCatalogItems.isNotEmpty() || isPretendingRefreshReturnedSomething) {
            log.i("In this reading ${newCatalogItems.size} newer items " +
                    "were fetched from the remote storage")

            //first destroy the whole local cache
            localCatalogSource.deleteAll()
            allLocalCatalogItems.clear()

            //add new stuff
            localCatalogSource.insertWithoutImage(newCatalogItems)
            allLocalCatalogItems += newCatalogItems
        }
        else {
            log.i("No newer items present on the remote storage")
            log.d("Nothing to do")
        }
    }
}
package com.marlove.catalog.data.repository.list

import com.marlove.catalog.data.repository.IRemoteCatalogSource
import com.marlove.catalog.domain.model.CatalogItem
import com.marlove.catalog.logger.log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext


class Refresher(private val allLocalCatalogItems: MutableList<CatalogItem>,
                private val remoteCatalog: IRemoteCatalogSource,
                private val dispatcher : CoroutineDispatcher )
{
    suspend fun refresh( )  =  withContext(dispatcher) {

        val firstId = allLocalCatalogItems.first().id
        log.d("Fetching from the remote storage all newer than  id = $firstId")
        val newCatalogItems = remoteCatalog.getItemsSinceID(firstId)

        if(newCatalogItems.isNotEmpty()) {
            log.d("In this reading ${newCatalogItems.size} newer items " +
                    "were fetched from the remote storage")

            //TODO No purpose since this functionality is missing on the site.
            //TODO Always returns empty list


        }
        else {
            log.d("No newer items present on the remote storage")
        }
    }
}
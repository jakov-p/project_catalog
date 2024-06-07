package com.marlove.catalog.data.source.remote

import com.marlove.catalog.data.repository.IRemoteCatalogSource
import com.marlove.catalog.data.source.remote.retrofit.ICatalogServiceApi
import com.marlove.catalog.data.source.remote.retrofit.utilities.CallPerformer
import com.marlove.catalog.domain.model.CatalogItem
import javax.inject.Inject

/**
 * Represents the remote source. In this case it an online site.
 *
 * The methods of this class reflect the API provided by the online site.
 *
 */
class RemoteCatalogSource @Inject constructor(val serviceApi : ICatalogServiceApi):
    IRemoteCatalogSource {

    override suspend fun getItemsSinceID(sinceId: String?): List<CatalogItem> {

        val commandDescription = "Get Catalog Items With Since Id Request, sinceId = $sinceId"
        val runner = CallPerformer(commandDescription) {
            serviceApi.getItemsSinceID(sinceId)
        }
        return  runner.perform().map { it.toCatalogItem() }
    }


    override suspend fun getItemsMaxId(maxId: String?): List<CatalogItem> {

        val commandDescription = "Get Catalog Items With Max Id Request, maxId = $maxId"
        val runner = CallPerformer(commandDescription) {
                serviceApi.getItemsMaxId(maxId)
        }
        return  runner.perform().map { it.toCatalogItem() }
    }
}
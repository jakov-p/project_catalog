package com.marlove.catalog.data.repository

import com.marlove.catalog.data.repository.details.CatalogItemDetailsProvider
import com.marlove.catalog.data.repository.list.CatalogItemsPager
import com.marlove.catalog.data.repository.list.Fetcher
import com.marlove.catalog.data.repository.list.Refresher
import com.marlove.catalog.data.source.remote.retrofit.utilities.FileDownloader
import com.marlove.catalog.domain.model.CatalogItem
import com.marlove.catalog.domain.repository.ICatalogItemDetailsProvider
import com.marlove.catalog.domain.repository.ICatalogItemsPager
import com.marlove.catalog.domain.repository.ICatalogRepository
import com.marlove.catalog.logger.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CatalogRepository @Inject constructor(
    private val localCatalog: ILocalCatalogSource,
    private val remoteCatalog: IRemoteCatalogSource

): ICatalogRepository
{
    private val dispatcher = Dispatchers.IO.limitedParallelism(1)

    init {
        log.d("${this::class.simpleName} initialized")
    }

    //this list always reflects the situation in the local storage
    private val allLocalCatalogItems: MutableList<CatalogItem> by lazy {
        localCatalog.getAllWithoutImage().toMutableList(). also {
            log.i("Initial reading from the local storage, items count = ${it.size} ")
        }
    }

    override fun getCatalogItems(pageSize:Int): ICatalogItemsPager =
        CatalogItemsPager(pageSize, ::getCatalogItems, ::refreshAll, ::closeAll)


    private suspend fun getCatalogItems(fromIndex: Int, loadSize: Int): List<CatalogItem> =
        withContext(dispatcher) {
            Fetcher(allLocalCatalogItems, localCatalog, remoteCatalog, dispatcher)
                .getCatalogItems(fromIndex, loadSize)
        }


    private suspend fun refreshAll( ) =
        Refresher(allLocalCatalogItems, localCatalog, remoteCatalog,dispatcher ).refresh()


    override  fun getCatalogItemDetails(id:String): ICatalogItemDetailsProvider =
        CatalogItemDetailsProvider(localCatalog, allLocalCatalogItems, {FileDownloader(it)}, dispatcher, id)


    private suspend fun closeAll( ) =  withContext(dispatcher) {
        // currently do nothing
    }

}
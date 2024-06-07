package com.marlove.catalog.data.repository.details

import com.marlove.catalog.data.repository.ILocalCatalogSource
import com.marlove.catalog.data.source.remote.retrofit.utilities.IFileDownloader
import com.marlove.catalog.data.source.remote.retrofit.utilities.IFileDownloaderFactory
import com.marlove.catalog.domain.model.CatalogItem
import com.marlove.catalog.domain.model.CatalogItemDetails
import com.marlove.catalog.domain.repository.ICatalogItemDetailsProvider
import com.marlove.catalog.domain.repository.ResultState
import com.marlove.catalog.logger.log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * Returns Catalog Item Details for a catalog item.
 *
 * It all comes down to the image bytes. All the details are already known, except for the contents
 * of the image itself. If it is not already in the local store, the images bytes
 * will be downloaded from the internet.
 *
 * @property localCatalogSource the local storage (dbase)
 * @property allCatalogItems the list of all known entries in the local storage
 * @property id the id of the catalog item whose details e are interested in.
 */
class CatalogItemDetailsProvider(private val localCatalogSource: ILocalCatalogSource,
                                 private val allCatalogItems :List<CatalogItem>,
                                 private val fileDownloaderFactory: IFileDownloaderFactory,
                                 private val dispatcher: CoroutineDispatcher,
                                 private val id:String): ICatalogItemDetailsProvider
{
    private var fileDownloader :IFileDownloader? = null

    private lateinit var imageUrl:String
    private lateinit var currentState:ResultState<CatalogItemDetails>


    override fun getFlow(): Flow<ResultState<CatalogItemDetails>> =

        flow {
            try {

                val foundCatalogItem = allCatalogItems.find { it.id == id }
                    ?: throw Exception("Catalog Item not found in the local storage, id = {it.id}")

                imageUrl =  foundCatalogItem.imageUrl

                val partialCatalogItemDetails = CatalogItemDetails(foundCatalogItem, null)
                emit2(ResultState.Running(partialCatalogItemDetails))

                val imageBytes = localCatalogSource.getImage(id)

                if(imageBytes != null) {
                    log.d("Image is already in the local storage , image = $imageUrl")
                    emit2(ResultState.Success(CatalogItemDetails(foundCatalogItem, imageBytes)))
                }
                else {
                    handleMisssingImage(foundCatalogItem)
                }
            }
            catch (e: Exception) {
                emit(ResultState.Error(e))
            }
        }.flowOn(dispatcher)


    /**
     *  Downloads the image if not in the local storage. If the download succeeds,
     *  the downloaded image is stored in the local storage.
     *  @param catalogItem
     */
    private suspend fun FlowCollector<ResultState<CatalogItemDetails>>.handleMisssingImage(
        catalogItem: CatalogItem)
     {
        runCatching {
            log.d("Image is not in the local storage.  image = $imageUrl")
            fileDownloader = fileDownloaderFactory.createFileDownloader(catalogItem.imageUrl)
            val imageBytes = fileDownloader!!.download().also {
                localCatalogSource.updateImage(id, it)
                log.d("Image is stored to the local storage , image = $imageUrl")
            }
            emit2(ResultState.Success(CatalogItemDetails(catalogItem, imageBytes)))

        }.getOrElse {

            log.e("Fetching image failed, image = $imageUrl", it)
            emit2(ResultState.Error(it, CatalogItemDetails(catalogItem, null)))
        }
    }


    override fun cancel() {

        if(currentState is ResultState.Running) {

            log.d("Canceling the flow for $imageUrl")
            fileDownloader?.let {
                log.d("The download has already started  for $imageUrl")
                log.d("Canceling the download....")
                it.cancel()
            }
        }
    }


    /**
     * Emits but also remembers the current state
     * @param resultState
     */
    private suspend fun FlowCollector<ResultState<CatalogItemDetails>>.
            emit2(resultState: ResultState<CatalogItemDetails>) {

        currentState = resultState
        this.emit(resultState)
    }

}
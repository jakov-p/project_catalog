package com.marlove.catalog.data.repository.list

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.marlove.catalog.domain.model.CatalogItem
import com.marlove.catalog.domain.model.CatalogItemDetails
import com.marlove.catalog.domain.repository.ICatalogItemsPager
import com.marlove.catalog.domain.repository.ResultState
import com.marlove.catalog.logger.log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

class CatalogItemsPager(pageSize:Int,
                        private val getItems: suspend (fromIndex:Int, loadSize:Int) -> List<CatalogItem>,
                        private val refreshAll: suspend () -> Unit,
                        private val closeAll: suspend () -> Unit,
    )
    : ICatalogItemsPager
{

    private val pagingConfig = PagingConfig(pageSize = pageSize,
        initialLoadSize = pageSize,
        prefetchDistance = pageSize,
        jumpThreshold = pageSize * 3,
        enablePlaceholders = true
    )

    private lateinit var catalogItemsPagingSource: CatalogItemsPagingSource

    private val pager = Pager(
        config = pagingConfig,
        pagingSourceFactory = {
            CatalogItemsPagingSource(getItems).also {
                catalogItemsPagingSource = it
            }
        }
    )

    override fun getPagingDataFlow(): Flow<PagingData<CatalogItem>> = pager.flow

    override fun refresh(): Flow<ResultState<Unit>> =
        flow  {
            try {
                emit2(ResultState.Running())

                refreshAll()
                catalogItemsPagingSource.invalidate()
                delay(1000) //TODO For testing

                emit2(ResultState.Success(Unit))

            } catch (ex: Exception) {
                emit2(ResultState.Error(ex))
            }
        }

    private suspend fun FlowCollector<ResultState<Unit>>.emit2(resultState: ResultState<Unit>) {
        log.d("Current refresh result = ${resultState} ")
        this.emit(resultState)
    }

    override suspend fun close() {
        closeAll()
    }
}


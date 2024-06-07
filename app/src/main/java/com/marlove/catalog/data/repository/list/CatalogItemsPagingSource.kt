package com.marlove.catalog.data.repository.list

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.marlove.catalog.logger.log
import com.marlove.catalog.domain.model.CatalogItem

/**
 * Returns the stream of Catalog Items in a paged manner.
 *
 * The approach here is that it is assumed that all the pages are of the same size (except
 * for the last one).
 *
 * @property getItems method that returns the Catalog Items in a specified range
 */
open class CatalogItemsPagingSource (
            private val getItems: suspend (fromIndex:Int, loadSize:Int) -> List<CatalogItem>)
    : PagingSource<Int, CatalogItem>()
{
    override val jumpingSupported = true

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CatalogItem> {
        try {
            val pageNumber = params.key ?: 0
            val fromIndex = pageNumber * params.loadSize
            val catalogItems = getItems(fromIndex, params.loadSize)

            return LoadResult.Page(
                data = catalogItems,
                prevKey = if (pageNumber > 0) pageNumber - 1 else null,
                nextKey = if (catalogItems.size == params.loadSize ) pageNumber + 1 else null
            ).also {
                log.d("$it")
            }
        }
        catch (ex: Exception) {

            log.e( "A failure when fetching data ", ex)
            return LoadResult.Error(ex)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CatalogItem>): Int? {

        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        } ?:run {
            log.d("state.anchorPosition == null")
            0
        }
    }
}



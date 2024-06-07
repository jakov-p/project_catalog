package com.marlove.catalog.domain.usecase

import com.marlove.catalog.domain.repository.ICatalogItemsPager
import com.marlove.catalog.domain.repository.ICatalogRepository
import javax.inject.Inject

/**
 * Business logic for fetching  Catalog Items
 *
 * @property catalogRepository  the source of data
 */
class GetCatalogItemsUseCase @Inject constructor (val catalogRepository: ICatalogRepository) {

    fun execute(pageSize:Int): ICatalogItemsPager
            = catalogRepository.getCatalogItems(pageSize)

}



package com.marlove.catalog.domain.usecase

import com.marlove.catalog.domain.repository.ICatalogItemDetailsProvider
import com.marlove.catalog.domain.repository.ICatalogRepository
import javax.inject.Inject

/**
 * Business logic for fetching a Catalog Item Details
 * @property catalogRepository the source of data
 */
class GetCatalogItemDetailsUseCase @Inject constructor(val catalogRepository: ICatalogRepository) {

    fun execute(id:String):ICatalogItemDetailsProvider{
        return catalogRepository.getCatalogItemDetails(id)
    }

}

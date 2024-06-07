package com.marlove.catalog.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marlove.catalog.domain.model.CatalogItemDetails
import com.marlove.catalog.domain.repository.ICatalogItemDetailsProvider
import com.marlove.catalog.domain.repository.ResultState
import com.marlove.catalog.domain.usecase.GetCatalogItemDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

typealias DetailsResultState = ResultState<CatalogItemDetails>

/**
 * Feeds the View with the Catalog Item Details for a particular Catalog Item
 */
@HiltViewModel
class ItemDetailsViewModel @Inject constructor
    (val getCatalogItemDetailsUseCase: GetCatalogItemDetailsUseCase):ViewModel() {

    //the current state of fetching action
    private val _itemDetailsResultState = MutableStateFlow<DetailsResultState>(ResultState.Unknown())
    val itemDetailsResultState: StateFlow<DetailsResultState> = _itemDetailsResultState

    //the id of the Item for which Details are to be provided
    private lateinit var idToFetch:String

    //interface returning the Catalog Item Details
    private var catalogItemDetailProvider: ICatalogItemDetailsProvider? = null

    /**
     * Inform for which Catalog Item the details are to be provided
     */
    fun init(id:String){
        idToFetch = id
        fetch()
    }

    /**
     * start fetching the details
     */
    fun fetch() {
        viewModelScope.launch{
            getCatalogItemDetailsUseCase.execute(idToFetch).let{
                catalogItemDetailProvider = it
                it.getFlow().collectLatest {
                    _itemDetailsResultState.value = it
                } //TODO better way to do
            }
        }
    }

    //stop fetching when the the user leaves the fragment
    override fun onCleared() {
        catalogItemDetailProvider?.let{
            it.cancel()
        }
    }
}
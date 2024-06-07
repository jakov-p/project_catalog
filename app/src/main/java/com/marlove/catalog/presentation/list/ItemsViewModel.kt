package com.marlove.catalog.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.marlove.catalog.domain.repository.ICatalogItemsPager
import com.marlove.catalog.domain.repository.ResultState
import com.marlove.catalog.domain.usecase.GetCatalogItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Provides the flow with paged Catalog Items
 * Also it supports refresh of the Catalog Items
 *
 * @param getCatalogItemsUseCase
 */
@HiltViewModel
class ItemsViewModel @Inject constructor(getCatalogItemsUseCase: GetCatalogItemsUseCase):ViewModel() {

    companion object const val PAGE_SIZE = 10

    private val catalogItemsPager: ICatalogItemsPager = getCatalogItemsUseCase.execute(PAGE_SIZE)

    val catalogItemsFlow = catalogItemsPager.getPagingDataFlow().
                           cachedIn(viewModelScope)

    var refresher = RefreshViewModelHelper()

    //Not used
    fun shutDown() {
        viewModelScope.launch {
            catalogItemsPager.close()
        }
    }



    //This class deals only with refresh action
    inner class RefreshViewModelHelper() {

        //current state of the refresh action
        private val _refreshState = MutableStateFlow<ResultState<Unit>>(ResultState.Unknown())
        val refreshState: StateFlow<ResultState<Unit>> = _refreshState

        fun refresh()
        {
            viewModelScope.launch {
                catalogItemsPager.refresh().collectLatest {
                    _refreshState.value = it
                }
            }
        }


        /*
       This method informs the Model Viewer that the View has already received
       (and processed accordingly) the result of the 'refresh' action.
        */
        fun uiConsumed() {
            //Reset the state to the initial one
            _refreshState.value = ResultState.Unknown()
        }
    }
}
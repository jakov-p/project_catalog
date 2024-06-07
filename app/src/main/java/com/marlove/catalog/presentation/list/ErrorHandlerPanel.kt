package com.marlove.catalog.presentation.list

import android.content.Context
import androidx.core.view.isVisible
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import com.marlove.catalog.databinding.ViewAndProgressBinding
import com.marlove.catalog.logger.log
import com.marlove.catalog.utilities.InternetConnectionChecker

/**
 * Handles any error that happens when fetching data to fill the list
 *
 * There are two cases which are handled in different ways:
 *  - error when no data has been fetched so far
 *  - error when the list already contains some elements, but it failed when
 *  new elements were fetched
 */
class ErrorHandlerPanel(private val context: Context,
                        private val binding: ViewAndProgressBinding,
                        private val listAdapter: PagingDataAdapter<*, *>)
{
    fun handleError(loadState: LoadState.Error) {

        val errorMessage = loadState.error.message.toString()
        log.e("An error has occurred, error = $errorMessage")

        val isNetworkConnected = InternetConnectionChecker(context.applicationContext).isConnected

        if(listAdapter.itemCount == 0 ) {
            binding.handleErrorIfListEmpty(errorMessage, isNetworkConnected)
        }
        else {
            binding.handleErrorIfLIstNotEmpty(errorMessage, isNetworkConnected)
        }
    }

    fun extractError(loadState: CombinedLoadStates): LoadState.Error? =
        when
        {
            loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
            loadState.append  is LoadState.Error -> loadState.append  as LoadState.Error
            loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
            else -> null
        }


    private fun ViewAndProgressBinding.
            handleErrorIfListEmpty(errorMessage:String, isNetworkConnected:Boolean) {

        listPlusBottom.list.isVisible = false
        retryButton.isVisible = true
        errorMessageTextView.isVisible = true

        listPlusBottom.bottomErrorInclude.root.isVisible = false

        if (!isNetworkConnected) {
            log.w("Internet connection failure.")
            errorMessageTextView.text = context.getString(com.marlove.catalog.R.string.check_internet_connection)
        } else {
            errorMessageTextView.text = errorMessage
        }
    }

    private fun ViewAndProgressBinding.
            handleErrorIfLIstNotEmpty(errorMessage:String, isNetworkConnected:Boolean) {

        retryButton.isVisible = false
        errorMessageTextView.isVisible = false

        listPlusBottom.bottomErrorInclude.root.isVisible = true

        val text:String
        if (!isNetworkConnected) {
            log.w("Internet connection failure.")
            text = context.getString(com.marlove.catalog.R.string.check_internet_connection) + ". " + errorMessage
        } else {
            text = errorMessage
        }
        listPlusBottom.bottomErrorInclude.errorPanelErrorMessageTextView.text = text
    }
}
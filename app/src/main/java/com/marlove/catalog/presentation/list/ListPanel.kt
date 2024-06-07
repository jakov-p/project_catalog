package com.marlove.catalog.presentation.list

import android.content.Context
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.marlove.catalog.databinding.ViewAndProgressBinding
import com.marlove.catalog.logger.log


/**
 * Shows or hides GUI control displaying 'loading in progress' and error.
 */
class ListPanel(private val context:Context,
                private val binding: ViewAndProgressBinding,
                private val listAdapter: PagingDataAdapter<*,*>)

{
    val itemsList: RecyclerView
        get(){ return binding.listPlusBottom.list }

    val mainRetryButton: AppCompatButton
        get(){ return binding.retryButton }

    val bottomRetryButton: AppCompatButton
        get(){ return binding.listPlusBottom.bottomErrorInclude.errorPanelRetryButton }

    val swiperefresh: SwipeRefreshLayout
        get(){ return binding.listPlusBottom.swiperefresh }

    init
    {
        itemsList.setHasFixedSize(true)
    }

    fun processState(loadStates:CombinedLoadStates) {

        with(binding)
        {
            log.i("A new CombinedLoadStates , loadStates =  $loadStates")

            showProgressBar(loadStates)

            // show an empty list
            val isListEmpty = (loadStates.refresh is LoadState.NotLoading) && listAdapter.itemCount==0
            noResultsTextView.isVisible = isListEmpty

            val errorHandlerPanel = ErrorHandlerPanel(context,binding, listAdapter)

            // Only show the list if refresh is successful.
            errorHandlerPanel.extractError(loadStates)?.let{
                errorHandlerPanel.handleError(it)
            }?: run {
                listPlusBottom.list.isVisible = (loadStates.source.refresh is LoadState.NotLoading)
                retryButton.isVisible = false
                errorMessageTextView.isVisible = false
                listPlusBottom.bottomErrorInclude.root.isVisible = false
            }
        }
    }

    // Show the progress bar while loading

    private fun ViewAndProgressBinding.showProgressBar(loadStates: CombinedLoadStates) {
        if (loadStates.source.refresh is LoadState.Loading   && listAdapter.itemCount==0) {
            progressbar.isVisible = true
            progressbar.setVerticalBias(0.5f)
        } else if (loadStates.source.append is LoadState.Loading) {
            progressbar.isVisible = true
            progressbar.setVerticalBias(0.92f)
        } else {
            progressbar.isVisible = false
        }
    }

    private fun ProgressBar.setVerticalBias (verticalBias: Float) {
        val params = this.layoutParams as ConstraintLayout.LayoutParams
        params.verticalBias = verticalBias
        this.setLayoutParams(params)
    }
}
package com.marlove.catalog.presentation.list

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.marlove.catalog.R
import com.marlove.catalog.domain.repository.ResultState
import com.marlove.catalog.logger.log
import com.marlove.catalog.utilities.Utilities.showLongToastMessage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Takes care of refreshing action
 *
 * @property fragment
 * @property swiperefresh
 * @property refreshViewModelHelper
 */
class Refresher(val fragment: Fragment,
                val swiperefresh: SwipeRefreshLayout,
                val refreshViewModelHelper: ItemsViewModel.RefreshViewModelHelper)
{
    fun init() {

        swiperefresh.setOnRefreshListener {

            log.d("##########################################################################")
            log.d("##########################################################################")
            log.d("*                                                                         *")
            log.d("*                    S W I P E   T O   R E F R E S H                      *")
            log.d("*                                                                         *")
            log.d("##########################################################################")
            log.d("##########################################################################")

            log.i("Refresh started...")
            refreshViewModelHelper.refresh()
        }

        //listen and react to the changes in a refresh action
        fragment.viewLifecycleOwner.lifecycleScope.launch {
            fragment.viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                refreshViewModelHelper.refreshState.collectLatest {
                    onRefreshStatusChange(it)
                }
            }
        }
    }

    private fun onRefreshStatusChange(resultState: ResultState<Unit>) {

        log.d("UI informed about new refresh action state, state = $resultState")

        //stop the UI refresh animation when the refresh action is done
        if (resultState is ResultState.Success || resultState is ResultState.Error) {

            log.d("Stopping the refresh indicator.")
            swiperefresh.isRefreshing = false

            /*
            This is necessary for rotation to inform that the GUI has been informed
            that the 'refresh' action has finished.
            Otherwise, on each rotation we would go through this same processing, which
            would result with showing the toast again and again.
             */
            refreshViewModelHelper.uiConsumed()

            if (resultState is ResultState.Error) {

                log.d("Informing the user that the refresh failed.")
                val toastText = fragment.requireContext().getString(R.string.refresh_failed) + ", Error = ${resultState.throwable.message}"
                showLongToastMessage(fragment.requireContext(), toastText)
            }
        }
        else {
            log.d("Ignoring the current refresh action state, state = $resultState")
        }
    }
}
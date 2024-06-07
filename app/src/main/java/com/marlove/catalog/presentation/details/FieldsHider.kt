package com.marlove.catalog.presentation.details

import androidx.core.view.isVisible
import com.marlove.catalog.databinding.ImageAndProgressBinding
import com.marlove.catalog.domain.repository.ResultState

/**
 * Hides and shows the UI elements based on the current status of
 * Catalog Item Details fetching action.
 *
 * @property imageAndProgressBinding
 */
class FieldsHider(val imageAndProgressBinding:ImageAndProgressBinding)
{
    fun show(resultState: DetailsResultState)
    {
        with(imageAndProgressBinding)
        {
            val visibilityStatus = resultState.calculateVisibilityStatus()

            imageView.isVisible = visibilityStatus.isImageShown
            imageProgressbar.isVisible = visibilityStatus.isProgressbarShown
            imageRetryButton.isVisible = visibilityStatus.isRetryButtonShown
            imageErrorMessageTextView.isVisible = visibilityStatus.isErrorMessageShown
        }
    }


    private fun DetailsResultState.calculateVisibilityStatus(): VisibilityStatus {
        return when(this){
            is ResultState.Unknown -> VisibilityStatus(isImageShown = true)
            is ResultState.Running -> VisibilityStatus(isProgressbarShown = true)
            is ResultState.Success -> VisibilityStatus(isImageShown = true)
            is ResultState.Error -> VisibilityStatus(isErrorMessageShown = true,
                                                      isRetryButtonShown = true)
        }
    }

    data class VisibilityStatus(
        val isImageShown:Boolean = false,
        val isProgressbarShown:Boolean = false,
        val isRetryButtonShown:Boolean = false,
        val isErrorMessageShown:Boolean = false
    )
}
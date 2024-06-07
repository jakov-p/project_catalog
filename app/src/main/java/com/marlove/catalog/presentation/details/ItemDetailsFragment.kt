package com.marlove.catalog.presentation.details

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.marlove.catalog.R
import com.marlove.catalog.databinding.FragmentDetailsBinding
import com.marlove.catalog.databinding.ImageAndProgressBinding
import com.marlove.catalog.domain.model.CatalogItemDetails
import com.marlove.catalog.domain.repository.ResultState
import com.marlove.catalog.logger.log
import com.marlove.catalog.utilities.Utilities.initToolbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val BUNDLE_PARAM_CATALOG_ID = "catalogId"

/**
 * Shows the Catalog Item Details for a particular Item.
 * The item's id is passed as a parameter to the fragment (in the arguments).
 */
@AndroidEntryPoint
class ItemDetailsFragment:Fragment() {

    private lateinit var binding : FragmentDetailsBinding
    private lateinit var imageAndProgressBinding : ImageAndProgressBinding

    private val itemDetailsViewModel: ItemDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(savedInstanceState == null) //the first time
        {
            arguments?.getString(BUNDLE_PARAM_CATALOG_ID)?.let {
                itemDetailsViewModel.init(it) //inform the View Model
            }
        }

        //start listening to the changes in the flow informing about the fetching of Item Details
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                itemDetailsViewModel.itemDetailsResultState.collectLatest {
                    imageAndProgressBinding.showDetails(it)
                }
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        imageAndProgressBinding = binding.imageInclude!!
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initToolbar(requireContext().getString(R.string.item_details_fragment_title))
    }

    private fun ImageAndProgressBinding.showDetails(resultState: DetailsResultState)
    {
        log.d("resultState = $resultState")
        FieldsHider(imageAndProgressBinding).show(resultState)

        when(resultState){

            is ResultState.Unknown -> {
                //at the beginning before the fetching starts
            }

            is ResultState.Running -> {
                //while still running we already have partial data (all fields but image bytes)
                fillAllButImage(resultState.data)
            }

            is ResultState.Success -> {

                fillAllButImage(resultState.data)

                val details:CatalogItemDetails = resultState.data
                log.d("The image size is ${details.imageBytes!!.size} bytes")

                BitmapFactory.decodeStream(details.imageBytes.inputStream())?.let {
                    imageView.setImageBitmap(it) //finally show the image
                }?: run {
                    log.e("Image bytes could not be converted into image, imageUrl = ${details.imageUrl}")
                }

            }

            is ResultState.Error -> {

                //Error usually means that image bytes are not available.
                //But still we have all other data returned.
                fillAllButImage(resultState.data)

                imageErrorMessageTextView.text = resultState.throwable.message
                imageRetryButton.setOnClickListener{
                    itemDetailsViewModel.fetch()
                }
            }
        }
    }

    /**
     * In the details only the image bytes are problematic. All other fields
     * are already available.
     * This method shows this other data.
     */
    private fun fillAllButImage(catalogItemDetails:CatalogItemDetails?) {

        catalogItemDetails?.let {
            with(binding) {
                detailsImageUrlTextView.text = it.imageUrl
                detailsIdTextView.text = it.id
                detailsTextTextView.text = it.text
                detailsConfidenceTextView.text = "%.2f".format(it.confidence)
            }
        }
    }
}

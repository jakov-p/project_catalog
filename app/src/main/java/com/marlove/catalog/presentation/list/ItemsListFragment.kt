package com.marlove.catalog.presentation.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.marlove.catalog.R
import com.marlove.catalog.databinding.FragmentListOfItemsBinding
import com.marlove.catalog.domain.model.CatalogItem
import com.marlove.catalog.presentation.details.BUNDLE_PARAM_CATALOG_ID
import com.marlove.catalog.utilities.Utilities.initToolbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Shows the list of catalog items
 */
@AndroidEntryPoint
class ItemsListFragment : Fragment() {

    private lateinit var binding : FragmentListOfItemsBinding

    private val itemsViewModel: ItemsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentListOfItemsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {

        //receives the flow of incoming Catalog Items
        val itemsListAdapter = ItemsListAdapter(requireContext(), ::onSelectedItem)

        //react to the flow of incoming Catalog Items
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                itemsViewModel.catalogItemsFlow.collectLatest {
                    //pass it into the UI list element
                    itemsListAdapter.submitData(it)
                }
            }
        }

        //all GUI control  are here inside
        val listPanel = ListPanel(requireContext(), binding.listInclude, itemsListAdapter)

        // initialize recyclerView
        listPanel.itemsList.adapter = itemsListAdapter
        itemsListAdapter.addLoadStateListener(listPanel::processState)

        //this 'retryButton' is shown in case when items list loading fails, and no
        //item has been fetched so far
        listPanel.mainRetryButton.setOnClickListener {
            itemsListAdapter.retry()
        }

        //this 'retryButton' is shown in case when items list loading fails, but the list
        //already contains some items (not empty)
        listPanel.bottomRetryButton.setOnClickListener {
            itemsListAdapter.retry()
        }

        Refresher(this, listPanel.swiperefresh,itemsViewModel.refresher).init()
    }

    /**
     * Called when the user clicks on a item in the catalog items list.
     * It leads to opening another fragment with details of that catalog item
     * @param catalogItem
     */
    private fun onSelectedItem(catalogItem: CatalogItem)  {

        val bundle = Bundle().apply {
            putString(BUNDLE_PARAM_CATALOG_ID, catalogItem.id)
        }
        findNavController().navigate(R.id.action_listOfItemsFragment_to_itemDetailsFragment, bundle)
    }

    override fun onResume() {
        super.onResume()
        initToolbar(requireContext().getString(R.string.list_of_items_fragment_title))
    }
}
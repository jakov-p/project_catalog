package com.marlove.catalog.presentation.list

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.marlove.catalog.R
import com.marlove.catalog.databinding.ItemBinding
import com.marlove.catalog.domain.model.CatalogItem
import com.marlove.catalog.utilities.ClickListenerHandler

/**
 * Receives the stream of Catalog Items and shows each Catalog Item
 * @param onSelectedItem = called when the user clicks on a catalog item
 */
class ItemsListAdapter(val context: Context,
                       val onSelectedItem:(catalogItem: CatalogItem) -> Unit):
    PagingDataAdapter<CatalogItem, ItemsListAdapter.ItemsListViewHolder>(DiffCallback)
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsListViewHolder {

        val binding = ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemsListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemsListViewHolder, position: Int) {

        getItem(position)?.let {
            holder.bind(it, position) }
        ?:
            holder.bindPlaceHolder()
    }


    inner class ItemsListViewHolder(val binding: ItemBinding) : ViewHolder(binding.root) {

        fun bind(catalogItem: CatalogItem, position: Int) {

            with(binding)
            {
                itemIdTextView.text =  catalogItem.id
                itemTextTextView.text =  catalogItem.text
                itemConfidenceTextView.text =  "%.2f".format(catalogItem.confidence)
                itemImageUrlTextView.text =  catalogItem.imageUrl

                //make this item clickable
                ClickListenerHandler(root, ::onSelectedItem).apply {
                    setClickListener(catalogItem, position)
                }

                setAlternateColor(position)
            }
        }

        fun onSelectedItem(catalogItem: CatalogItem, position: Int) {

            //flash for a moment with a different color
            binding.root.setBackgroundColor(context.getColor(R.color.item_selection))
            Handler(context.mainLooper).postDelayed({
                setAlternateColor(position)
            }, 200)

            //send the event
            onSelectedItem(catalogItem)
        }


        /**
         *  Update the background color according to the odd/even positions in the list.
         *  @param position position in the list
         */
        private fun setAlternateColor(position: Int) {

            val color = if (position % 2 == 0)
                                context.getColor(R.color.item_normal)
                            else
                                context.getColor(R.color.item_normal_alternate)
            binding.root.setBackgroundColor( color)
        }

        fun bindPlaceHolder() {
            //nothing here to be done
        }

    }

    object DiffCallback: DiffUtil.ItemCallback<CatalogItem>() {

        override fun areItemsTheSame(oldItem: CatalogItem, newItem: CatalogItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CatalogItem, newItem: CatalogItem): Boolean {
            return oldItem == newItem
        }
    }
}
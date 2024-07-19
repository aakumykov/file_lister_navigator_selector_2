package com.github.aakumykov.file_lister_navigator_selector.file_selector

import android.view.View
import android.view.ViewGroup
import com.github.aakumykov.file_lister_navigator_selector.R
import com.github.aakumykov.file_lister_navigator_selector.entities.Storage
import com.github.aakumykov.file_lister_navigator_selector.utils.ListHoldingListAdapter

class StorageListAdapter : ListHoldingListAdapter<Storage, StorageListItemViewHolder>(R.layout.storage_list_item_selected)
{
    override fun createViewHolder(itemView: View): ViewHolder<Storage> {
        return StorageListItemViewHolder()
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return getViewFromResource(R.layout.storage_list_item_drop_down, position, convertView, parent)
    }
}
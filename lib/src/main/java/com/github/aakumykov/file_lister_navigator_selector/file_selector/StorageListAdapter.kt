package com.github.aakumykov.file_lister_navigator_selector.file_selector

import android.view.View
import com.github.aakumykov.file_lister_navigator_selector.R
import com.github.aakumykov.file_lister_navigator_selector.entities.Storage
import com.github.aakumykov.file_lister_navigator_selector.utils.ListHoldingListAdapter

class StorageListAdapter : ListHoldingListAdapter<Storage, StorageListItemViewHolder>(R.layout.storage_list_item)
{
    override fun createViewHolder(itemView: View): ViewHolder<Storage> {
        return StorageListItemViewHolder()
    }
}
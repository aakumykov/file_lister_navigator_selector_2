package com.github.aakumykov.file_lister_navigator_selector.storage_selecting_dialog

import androidx.annotation.ColorRes
import com.github.aakumykov.file_lister_navigator_selector.R
import com.github.aakumykov.list_holding_list_adapter.ListHoldingListAdapter
import com.github.aakumykov.storage_lister.StorageDirectory

class StorageListAdapter(
    @ColorRes private val selectedItemBg: Int = R.color.selected_item_bg
)
    : ListHoldingListAdapter<StorageDirectory, StorageListViewHolder>(R.layout.storage_list_item)
{
    override fun createViewHolder(): ViewHolder<StorageDirectory> {
        return StorageListViewHolder(selectedItemBg)
    }
}

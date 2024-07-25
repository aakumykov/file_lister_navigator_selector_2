package com.github.aakumykov.storage_selector

import androidx.annotation.ColorRes
import com.github.aakumykov.list_holding_list_adapter.ListHoldingListAdapter

class StorageListAdapter(@ColorRes private val selectedItemBg: Int = R.color.selected_item_bg)
    : ListHoldingListAdapter<StorageWithIcon, StorageListViewHolder>(R.layout.storage_list_item)
{
    override fun createViewHolder(): ViewHolder<StorageWithIcon> {
        return StorageListViewHolder(selectedItemBg)
    }
}

package com.github.aakumykov.file_lister_navigator_selector.file_selector

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.github.aakumykov.file_lister_navigator_selector.R
import com.github.aakumykov.file_lister_navigator_selector.entities.Storage
import com.github.aakumykov.file_lister_navigator_selector.utils.ListHoldingListAdapter

class StorageListItemViewHolder : ListHoldingListAdapter.ViewHolder<Storage>() {

    private lateinit var storageListItemView: TextView

    override fun init(itemView: View) {
        storageListItemView = itemView.findViewById(R.id.storageLabelView)
    }

    override fun fill(item: Storage) {
        storageListItemView.apply {
            text = item.label
            setCompoundDrawablesWithIntrinsicBounds(item.iconId, 0, 0, 0);
        }
    }
}

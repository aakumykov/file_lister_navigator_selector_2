package com.github.aakumykov.file_lister_navigator_selector.file_selector

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.setPadding
import com.github.aakumykov.file_lister_navigator_selector.R
import com.github.aakumykov.file_lister_navigator_selector.entities.Storage
import com.github.aakumykov.file_lister_navigator_selector.extensions.colorize
import com.github.aakumykov.file_lister_navigator_selector.extensions.setColorResource
import com.github.aakumykov.file_lister_navigator_selector.utils.ListHoldingListAdapter

class StorageListItemViewHolder : ListHoldingListAdapter.ViewHolder<Storage>() {

    private lateinit var titleView: TextView
    private lateinit var iconView: ImageView

    override fun init(itemView: View) {
        titleView = itemView.findViewById(R.id.titleView)
        iconView = itemView.findViewById(R.id.iconView)
    }

    override fun fill(item: Storage) {
        titleView.text = item.label
        iconView.setImageResource(item.iconId)

        com.google.android.material.R.color.design_default_color_on_primary.also { colorRes ->
            titleView.setColorResource(colorRes)
            iconView.colorize(colorRes)
        }

        titleView.setPadding(0)
    }

    override fun fillAsDropDown(item: Storage) {
        fill(item)

        com.google.android.material.R.color.design_default_color_primary.also { colorRes ->
            titleView.setColorResource(colorRes)
            iconView.colorize(colorRes)
        }

        titleView.setPadding(8)
    }
}

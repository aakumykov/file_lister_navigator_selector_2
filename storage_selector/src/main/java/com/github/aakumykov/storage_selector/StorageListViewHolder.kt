package com.github.aakumykov.storage_selector

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import com.github.aakumykov.list_holding_list_adapter.ListHoldingListAdapter

class StorageListViewHolder(
    @ColorRes private val selectedItemBg: Int
) : ListHoldingListAdapter.ViewHolder<Storage>() {

    private lateinit var listItemView: View
    private lateinit var iconView: ImageView
    private lateinit var nameView: TextView


    override fun init(itemView: View) {
        this.listItemView = itemView.findViewById(R.id.listItemView)
        this.iconView = itemView.findViewById(R.id.iconView)
        this.nameView = itemView.findViewById(R.id.nameView)
    }


    override fun fill(item: Storage, isSelected: Boolean) {
        this.iconView.setImageResource(item.icon)
        this.nameView.text = item.name
        decorateAsSelected(isSelected)
    }


    override fun fillAsDropDown(item: Storage, isSelected: Boolean) {
        fill(item, isSelected)
    }


    private fun decorateAsSelected(isSelected: Boolean) {
        Log.d(TAG, "decorateAsSelected($isSelected)")
        listItemView.setBackgroundColor(when(isSelected){
            true -> getColor(selectedItemBg)
            false -> android.R.attr.background
        })
    }

    @ColorInt
    private fun getColor(@ColorRes colorRes: Int): Int {
        return listItemView.context.let { context ->
            ResourcesCompat.getColor(context.resources, selectedItemBg, context.theme)
        }
    }


    companion object {
        val TAG: String = StorageListViewHolder::class.java.simpleName
    }
}

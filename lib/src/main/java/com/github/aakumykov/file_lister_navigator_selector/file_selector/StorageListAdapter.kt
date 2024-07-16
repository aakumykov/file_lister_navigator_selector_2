package com.github.aakumykov.file_lister_navigator_selector.file_selector

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import com.github.aakumykov.file_lister_navigator_selector.entities.Storage
import com.github.aakumykov.file_lister_navigator_selector.utils.ListAdapter

class StorageListAdapter(
    context: Context,
    list: List<Storage>,
//    @LayoutRes private val itemLayout: Int,
//    @LayoutRes private val dropdownItemLayout: Int? = null,
)
    : ListAdapter(
        context = context,
        resourceWithTitleView = android.R.layout.simple_spinner_item,
        list = list
    )
{
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }
}
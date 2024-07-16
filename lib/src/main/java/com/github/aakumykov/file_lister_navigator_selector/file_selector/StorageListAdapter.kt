package com.github.aakumykov.file_lister_navigator_selector.file_selector

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import com.github.aakumykov.file_lister_navigator_selector.entities.Storage

class StorageListAdapter(
    context: Context,
    @LayoutRes private val itemLayout: Int,
    @LayoutRes private val dropdownItemLayout: Int? = null,
)
    : ArrayAdapter<Storage>(context, itemLayout)
{
    init {
        dropdownItemLayout?.also { setDropDownViewResource(dropdownItemLayout) }
    }

    private var list: MutableList<Storage> = mutableListOf()

    override fun getCount(): Int = list.size

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).inflate(itemLayout, parent, false)
    }

    fun setNewList(storageList: List<Storage>) {
        this.list.clear()
        this.list.addAll(storageList)
    }

    /*override fun getPosition(item: Storage?): Int {
        return super.getPosition(item)
    }*/

    /*override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }*/

    /*override fun getItem(position: Int): Storage? {
        return super.getItem(position)
    }*/
}
package com.github.aakumykov.file_lister_navigator_selector_demo.common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.github.aakumykov.file_lister_navigator_selector.R
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem

class ListAdapter (
    context: Context,
    private val resource: Int,
    private val list: List<FSItem>
)
    : ArrayAdapter<FSItem>(context, resource, list)
{
    private val inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val viewHolder: ViewHolder

        val itemView = if (convertView == null) {
            val itemView = inflater.inflate(resource, parent, false)
            viewHolder = ViewHolder(itemView)
            itemView.tag = viewHolder
            itemView
        }
        else {
            viewHolder = convertView.tag as ViewHolder
            convertView
        }

        val fsItem: FSItem = list[position]

        viewHolder.titleView.text = when {
            fsItem.isDir -> "[${fsItem.name}]"
            else -> fsItem.name
        }

        return itemView
    }

    private inner class ViewHolder(view: View) {

        val titleView: TextView

        init {
            titleView = view.findViewById(R.id.titleView)
        }
    }
}
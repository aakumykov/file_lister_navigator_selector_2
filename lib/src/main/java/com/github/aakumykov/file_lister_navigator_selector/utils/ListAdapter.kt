package com.github.aakumykov.file_lister_navigator_selector.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import android.widget.TextView
import com.github.aakumykov.file_lister_navigator_selector.R

open class ListAdapter (
    context: Context,
    private val resourceWithTitleView: Int,
    private val list: List<TitleItem>
)
    : ArrayAdapter<ListAdapter.TitleItem>(context, resourceWithTitleView, list),
    SpinnerAdapter
{
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val viewHolder: ViewHolder

        val itemView = if (convertView == null) {
            val itemView = inflater.inflate(resourceWithTitleView, parent, false)
            viewHolder = ViewHolder(itemView)
            itemView.tag = viewHolder
            itemView
        }
        else {
            viewHolder = convertView.tag as ViewHolder
            convertView
        }

        viewHolder.titleView.text = list[position].getTitle()

        return itemView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }

    private inner class ViewHolder(view: View) {

        val titleView: TextView

        init {
            titleView = view.findViewById(R.id.titleView)
        }
    }

    interface TitleItem {
        fun getTitle(): String
    }

    class SimpleTitleItem(private val title: String) : TitleItem {
        override fun getTitle(): String = title
    }
}
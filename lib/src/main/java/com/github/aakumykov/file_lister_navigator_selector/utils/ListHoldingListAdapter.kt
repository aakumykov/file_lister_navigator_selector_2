package com.github.aakumykov.file_lister_navigator_selector.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.annotation.LayoutRes

/**
 * Адаптер списка с внутренним списком элементов.
 */
abstract class ListHoldingListAdapter<T, V: ListHoldingListAdapter.ViewHolder<T>>(
    @LayoutRes private val itemLayoutResourceId: Int,
)
    : BaseAdapter()
{
    private val list: MutableList<T> = mutableListOf()


    abstract fun createViewHolder(itemView: View): ViewHolder<T>


    fun setList(newList: List<T>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }


    fun clearList() {
        list.clear()
        notifyDataSetChanged()
    }

    fun addItem(item: T) {
        list.add(item)
        notifyDataSetChanged()
    }

    fun removeItem(item: T) {
        list.remove(item)
        notifyDataSetChanged()
    }

    fun removeItemAt(position: Int) {
        list.removeAt(position)
        notifyDataSetChanged()
    }


    override fun getCount(): Int {
        return list.size
    }


    override fun getItem(position: Int): T {
        return list[position]
    }


    override fun getItemId(position: Int): Long {
        return list[position].hashCode().toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return getViewFromResource(itemLayoutResourceId, position, convertView, parent)
    }

    protected fun getViewFromResource(layoutResId: Int, position: Int, convertView: View?, parent: ViewGroup?): View {

        val viewHolder: ViewHolder<T>

        val itemView =
            if (convertView == null) {
                val itemView = LayoutInflater.from(parent!!.context).inflate(layoutResId, parent, false)
                viewHolder = createViewHolder(itemView).apply { init(itemView) }
                itemView.tag = viewHolder
                itemView
            }
            else {
                viewHolder = convertView.tag as ViewHolder<T>
                convertView
            }

        viewHolder.fill(list[position])

        return itemView
    }


    abstract class ViewHolder<ItemType> {
        abstract fun init(itemView: View)
        abstract fun fill(item: ItemType)
    }
}
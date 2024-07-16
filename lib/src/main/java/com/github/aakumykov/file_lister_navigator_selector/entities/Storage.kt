package com.github.aakumykov.file_lister_navigator_selector.entities

import com.github.aakumykov.file_lister_navigator_selector.utils.ListAdapter

data class Storage(
    val name: String,
    val absolutePath: String,
) : ListAdapter.TitleItem {
    override fun getTitle(): String = name
}
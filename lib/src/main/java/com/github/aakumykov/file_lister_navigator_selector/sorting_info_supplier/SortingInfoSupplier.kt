package com.github.aakumykov.file_lister_navigator_selector.sorting_info_supplier

import android.content.Context
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem

interface SortingInfoSupplier<SortingModeType> {
    fun getSortingInfo(
        context: Context,
        fsItem: FSItem,
        sortingMode: SortingModeType,
        prefix: String = "",
        suffix: String = ""
    ): String
}
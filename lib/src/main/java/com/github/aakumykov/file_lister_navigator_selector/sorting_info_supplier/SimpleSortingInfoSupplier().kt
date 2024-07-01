package com.github.aakumykov.file_lister_navigator_selector.sorting_info_supplier

import android.content.Context
import android.text.format.Formatter
import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.file_lister_navigator_selector.utils.DateFormatter

// TODO: внудрять разные "предоставители информации"
class SimpleSortingInfoSupplier : SortingInfoSupplier<SimpleSortingMode> {

    override fun getSortingInfo(
        context: Context,
        fsItem: FSItem,
        sortingMode: SimpleSortingMode,
        prefix: String,
        suffix: String
    ): String {

        val info = when(sortingMode) {
            SimpleSortingMode.NAME -> ""
            SimpleSortingMode.SIZE -> humanReadableSize(context, fsItem.size)
            SimpleSortingMode.C_TIME -> DateFormatter.humanReadableDate(fsItem.mTime)
            SimpleSortingMode.M_TIME -> DateFormatter.humanReadableDate(fsItem.mTime)
        }

        return if (info.isEmpty()) info else prefix + info + suffix
    }

    private fun humanReadableSize(context: Context, bytes: Long): String {
        return Formatter
            .formatShortFileSize(context, bytes)
//            .formatFileSize(context, bytes)
    }
}
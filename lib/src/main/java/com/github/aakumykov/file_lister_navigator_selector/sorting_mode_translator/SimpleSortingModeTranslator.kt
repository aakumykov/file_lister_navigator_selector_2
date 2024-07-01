package com.github.aakumykov.file_lister_navigator_selector.sorting_mode_translator

import android.content.res.Resources
import androidx.annotation.StringRes
import com.github.aakumykov.file_lister_navigator_selector.R
import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode

class SimpleSortingModeTranslator(
    private val resources: Resources,
    private val directOrderIcon: String = "⬇\uFE0F",
    private val reverseOrderIcon: String = "⬆\uFE0F"
) :
    SortingModeTranslator<SimpleSortingMode> {

    override fun sortingModeNames(currentMode: SimpleSortingMode, isReverseOrder: Boolean): Array<String> {
        return SimpleSortingMode.entries.map { sortingMode ->

            var name = resources.getString(sortingMode.sortingName)

            name += " " + if (currentMode == sortingMode) {
                if (isReverseOrder) reverseOrderIcon
                else directOrderIcon
            } else ""

            return@map name

        }.toTypedArray()
    }

    override fun sortingNameToSortingMode(sortingModeName: String): SimpleSortingMode? {
        return when(sortingModeName) {
            s(R.string.sorting_mode_name) -> SimpleSortingMode.NAME
            s(R.string.sorting_mode_size) -> SimpleSortingMode.SIZE
            s(R.string.sorting_mode_c_time) -> SimpleSortingMode.C_TIME
            s(R.string.sorting_mode_m_time) -> SimpleSortingMode.M_TIME
            else -> null
        }
    }

    override fun sortingModeToPosition(mode: SimpleSortingMode): Int {
        return SimpleSortingMode.entries.indexOf(mode)
    }

    override fun positionToSortingMode(position: Int): SimpleSortingMode {
        return SimpleSortingMode.entries[position]
    }

    private fun s(@StringRes strRes: Int): String {
        return resources.getString(strRes)
    }
}
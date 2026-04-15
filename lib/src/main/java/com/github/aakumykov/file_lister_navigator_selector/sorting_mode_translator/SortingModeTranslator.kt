package com.github.aakumykov.file_lister_navigator_selector.sorting_mode_translator

import androidx.annotation.IdRes

interface SortingModeTranslator<SortingModeType> {

    fun sortingModeNames(currentMode: SortingModeType, isReverseOrder: Boolean): Array<String>
    fun sortingNameToSortingMode(sortingModeName: String): SortingModeType?

    fun sortingModeToPosition(mode: SortingModeType): Int
    fun positionToSortingMode(position: Int): SortingModeType

    fun viewId2sortingMode(@IdRes viewId: Int): SortingModeType
    @IdRes fun sortingMode2viewId(sortingMode: SortingModeType): Int

    fun string2sortingMode(s: String): SortingModeType
}
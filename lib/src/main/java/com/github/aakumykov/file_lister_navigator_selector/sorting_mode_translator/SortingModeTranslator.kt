package com.github.aakumykov.file_lister_navigator_selector.sorting_mode_translator

interface SortingModeTranslator<SortingModeType> {

    fun sortingModeNames(currentMode: SortingModeType, isReverseOrder: Boolean): Array<String>
    fun sortingNameToSortingMode(sortingModeName: String): SortingModeType?

    fun sortingModeToPosition(mode: SortingModeType): Int
    fun positionToSortingMode(position: Int): SortingModeType
}
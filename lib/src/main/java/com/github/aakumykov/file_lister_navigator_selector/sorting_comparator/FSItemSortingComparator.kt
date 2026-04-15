package com.github.aakumykov.file_lister_navigator_selector.sorting_comparator

import com.github.aakumykov.extensible_sorting_comparator.ExtensibleSortingComparator
import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem

abstract class FSItemSortingComparator(directOrder: Boolean, foldersFirst: Boolean)
    : ExtensibleSortingComparator<FSItem>(directOrder, foldersFirst)
{
    override fun isPriorityItem(item: FSItem): Boolean = item.isDir

    companion object {
        fun create(sortingMode: SimpleSortingMode,
                   isDirectOrder: Boolean = false,
                   foldersFirst: Boolean = true
        ): FSItemSortingComparator {
            return when(sortingMode) {
                SimpleSortingMode.NAME -> NameSortingComparator(isDirectOrder, foldersFirst)
                SimpleSortingMode.SIZE -> SizeSortingComparator(isDirectOrder, foldersFirst)
                SimpleSortingMode.M_TIME -> TimeSortingComparator(isDirectOrder, foldersFirst)
                else -> DummySortingComparator()
            }
        }
    }
}


class NameSortingComparator(directOrder: Boolean, foldersFirst: Boolean) : FSItemSortingComparator(directOrder, foldersFirst) {
    override fun compareItems(item1: FSItem, item2: FSItem): Int {
        return item1.name.compareTo(item2.name)
    }
}


class TimeSortingComparator(directOrder: Boolean, foldersFirst: Boolean) : FSItemSortingComparator(directOrder, foldersFirst) {
    override fun compareItems(item1: FSItem, item2: FSItem): Int {
        return item1.mTime.compareTo(item2.mTime)
    }
}


class SizeSortingComparator(directOrder: Boolean, foldersFirst: Boolean) : FSItemSortingComparator(directOrder, foldersFirst) {
    override fun compareItems(item1: FSItem, item2: FSItem): Int {
        return item1.size.compareTo(item2.size)
    }
}


class DummySortingComparator : FSItemSortingComparator(true, true) {
    override fun compareItems(item1: FSItem, item2: FSItem): Int {
        return 0
    }
}
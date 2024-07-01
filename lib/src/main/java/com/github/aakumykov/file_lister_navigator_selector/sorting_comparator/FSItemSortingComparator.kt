package com.github.aakumykov.file_lister_navigator_selector.sorting_comparator

import com.github.aakumykov.extensible_sorting_comparator.ExtensibleSortingComparator
import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem

abstract class FSItemSortingComparator(reverseOrder: Boolean, foldersFirst: Boolean)
    : ExtensibleSortingComparator<FSItem>(reverseOrder, foldersFirst)
{
    override fun isPriorityItem(item: FSItem): Boolean = item.isDir

    companion object {
        fun create(sortingMode: SimpleSortingMode,
                   isReverseOrder: Boolean = false,
                   foldersFirst: Boolean = true
        ): FSItemSortingComparator {
            return when(sortingMode) {
                SimpleSortingMode.NAME -> NameSortingComparator(isReverseOrder, foldersFirst)
                SimpleSortingMode.SIZE -> SizeSortingComparator(isReverseOrder, foldersFirst)
                SimpleSortingMode.M_TIME -> TimeSortingComparator(isReverseOrder, foldersFirst)
                else -> DummySortingComparator()
            }
        }
    }
}


class NameSortingComparator(reverseOrder: Boolean, foldersFirst: Boolean) : FSItemSortingComparator(reverseOrder, foldersFirst) {
    override fun compareItems(item1: FSItem, item2: FSItem): Int {
        return item1.name.compareTo(item2.name)
    }
}


class TimeSortingComparator(reverseOrder: Boolean, foldersFirst: Boolean) : FSItemSortingComparator(reverseOrder, foldersFirst) {
    override fun compareItems(item1: FSItem, item2: FSItem): Int {
        return item1.mTime.compareTo(item2.mTime)
    }
}


class SizeSortingComparator(reverseOrder: Boolean, foldersFirst: Boolean) : FSItemSortingComparator(reverseOrder, foldersFirst) {
    override fun compareItems(item1: FSItem, item2: FSItem): Int {
        return item1.size.compareTo(item2.size)
    }
}


class DummySortingComparator : FSItemSortingComparator(true, true) {
    override fun compareItems(item1: FSItem, item2: FSItem): Int {
        return 0
    }
}
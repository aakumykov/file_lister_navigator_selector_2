package com.github.aakumykov.file_lister_navigator_selector.fs_item.utils

import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem

fun parentPathFor(path: String): String {
    return when(path) {
        FSItem.PARENT_DIR_PATH -> path
        FSItem.NO_PARENT_PATH -> path
        else -> {
            if (path.contains(FSItem.DS))
                with(path.split(FSItem.DS)) { this.subList(0, this.size - 1).joinToString(FSItem.DS) }
            else
                path
        }
    }
}
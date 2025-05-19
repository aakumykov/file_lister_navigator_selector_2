package com.github.aakumykov.local_file_lister_navigator_selector.local_fs_navigator

import android.os.Environment
import com.github.aakumykov.file_lister_navigator_selector.file_explorer.BasicFileExplorer
import com.github.aakumykov.file_lister_navigator_selector.file_explorer.FileExplorer
import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode
import com.github.aakumykov.file_lister_navigator_selector.fs_item.DirItem
import com.github.aakumykov.local_file_lister_navigator_selector.local_dir_creator.LocalDirCreator
import com.github.aakumykov.local_file_lister_navigator_selector.local_file_lister.LocalFileLister

class LocalFileExplorer(
    localFileLister: LocalFileLister,
    localDirCreator: LocalDirCreator,
    initialPath: String,
    isDirMode: Boolean = false,
    defaultSortingMode: SimpleSortingMode = SimpleSortingMode.NAME,
    listCache: FileExplorer.ListCache? = null,
    pathCache: FileExplorer.PathCache? = null
)
: BasicFileExplorer<SimpleSortingMode> (
    fileLister = localFileLister,
    dirCreator = localDirCreator,
    initialPath = initialPath,
    isDirMode = isDirMode,
    initialSortingMode = defaultSortingMode,
    listCache = listCache,
    pathCache = pathCache
) {
    override fun getHomeDir(): DirItem {
        return DirItem(Environment.getExternalStorageDirectory())
    }
}
package com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_fs_navigator

import com.github.aakumykov.file_lister_navigator_selector.file_explorer.BasicFileExplorer
import com.github.aakumykov.file_lister_navigator_selector.file_explorer.FileExplorer
import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode
import com.github.aakumykov.file_lister_navigator_selector.fs_item.DirItem
import com.github.aakumykov.file_lister_navigator_selector.fs_item.RootDirItem
import com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_dir_creator.YandexDiskDirCreator
import com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_file_lister.YandexDiskFileLister
import java.io.File

class YandexDiskFileExplorer (
    yandexDiskFileLister: YandexDiskFileLister,
    yandexDiskDirCreator: YandexDiskDirCreator,
    initialPath: String,
    isDirMode: Boolean = false,
    defaultSortingMode: SimpleSortingMode = SimpleSortingMode.NAME,
    listCache: FileExplorer.ListCache? = null,
    pathCache: FileExplorer.PathCache? = null
)
: BasicFileExplorer<SimpleSortingMode>(
    fileLister = yandexDiskFileLister,
    dirCreator = yandexDiskDirCreator,
    initialPath = initialPath,
    isDirMode = isDirMode,
    initialSortingMode = defaultSortingMode,
    listCache = listCache,
    pathCache = pathCache
) {
    override fun getHomeDir(): DirItem = RootDirItem()

    override val defaultListingLimit: Int = yandexDiskFileLister.defaultListingLimit
}
package com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_file_lister

import com.github.aakumykov.cloud_reader.FileMetadata
import com.github.aakumykov.file_lister_navigator_selector.file_lister.FileLister
import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.file_lister_navigator_selector.fs_item.SimpleFSItem
import com.github.aakumykov.file_lister_navigator_selector.fs_item.utils.parentPathFor
import com.github.aakumykov.file_lister_navigator_selector.sorting_comparator.FSItemSortingComparator
import com.github.aakumykov.yandex_disk_cloud_reader.YandexDiskCloudReader
import kotlinx.coroutines.runBlocking

class YandexDiskFileLister(
    private val authToken: String,
    private val yandexCloudReader: YandexDiskCloudReader = YandexDiskCloudReader(authToken)
)
    : FileLister<SimpleSortingMode>
{
    companion object {
        private const val DEFAULT_STARTING_OFFSET = 0
        private const val DEFAULT_LISTING_LIMIT = 5
    }

    override val defaultListingOffset: Int = DEFAULT_STARTING_OFFSET

    override val defaultListingLimit: Int = DEFAULT_LISTING_LIMIT


    /**
     * Параметр foldersFirst не имеет эффекта.
     */
    override fun listDir(
        path: String,
        sortingMode: SimpleSortingMode,
        reverseOrder: Boolean,
        foldersFirst: Boolean,
        dirMode: Boolean,
        offset: Int,
        limit: Int
    )
        : List<FSItem>
    {
        return runBlocking {
            yandexCloudReader
                .listDir(path, offset, limit)
                .getOrThrow()
                ?.map { fileMetadata: FileMetadata ->
                    val simpleFSItem = SimpleFSItem(
                        name = fileMetadata.name,
                        absolutePath = fileMetadata.absolutePath,
                        parentPath = parentPathFor(fileMetadata.absolutePath),
                        isDir = fileMetadata.isDir,
                        mTime = fileMetadata.modified,
                        size = fileMetadata.size
                    )
                    convertDirToDirItem(simpleFSItem)
                } ?: emptyList()
        }.filter {
            // TODO: вынести этот фильтр в единое место
            if (dirMode) it.isDir
            else true
        }.sortedWith(
            FSItemSortingComparator.create(sortingMode, reverseOrder, foldersFirst)
        )
    }

    override suspend fun fileExists(path: String): Result<Boolean> = yandexCloudReader.fileExists(path)
}

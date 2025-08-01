package com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_file_lister

import com.github.aakumykov.cloud_reader.FileMetadata
import com.github.aakumykov.file_lister_navigator_selector.file_lister.FileLister
import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.file_lister_navigator_selector.fs_item.SimpleFSItem
import com.github.aakumykov.file_lister_navigator_selector.fs_item.utils.parentPathFor
import com.github.aakumykov.yandex_disk_cloud_reader.YandexDiskCloudReader
import kotlinx.coroutines.runBlocking

class YandexDiskFileLister(
    private val authToken: String,
    private val yandexCloudReader: YandexDiskCloudReader = YandexDiskCloudReader(authToken)
)
    : FileLister<SimpleSortingMode>
{
    /**
     * Параметр foldersFirst не имеет эффекта.
     */
    override fun listDir(
        path: String,
        sortingMode: SimpleSortingMode,
        reverseOrder: Boolean,
        foldersFirst: Boolean,
        dirMode: Boolean
    )
        : List<FSItem>
    {
        return runBlocking {
            yandexCloudReader
                .listDir(path)
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
        }
    }

    override suspend fun fileExists(path: String): Result<Boolean> = yandexCloudReader.fileExists(path)
}

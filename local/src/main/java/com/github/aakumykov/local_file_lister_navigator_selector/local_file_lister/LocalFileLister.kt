package com.github.aakumykov.local_file_lister_navigator_selector.local_file_lister

import com.github.aakumykov.file_lister_navigator_selector.file_lister.FileLister
import com.github.aakumykov.file_lister_navigator_selector.file_lister.FileLister.CannotReadException
import com.github.aakumykov.file_lister_navigator_selector.file_lister.FileLister.NotADirException
import com.github.aakumykov.file_lister_navigator_selector.file_lister.FileLister.OtherDirListingException
import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.file_lister_navigator_selector.fs_item.SimpleFSItem
import com.github.aakumykov.file_lister_navigator_selector.sorting_comparator.FSItemSortingComparator
import com.github.aakumykov.local_cloud_reader.LocalCloudReader
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class LocalFileLister(
    private val dummyAuthToken: String = "",
    private val localCloudReader: LocalCloudReader = LocalCloudReader()
)
    : FileLister<SimpleSortingMode>
{
    companion object {
        private const val DEFAULT_STARTING_OFFSET = 0
        private const val DEFAULT_LISTING_LIMIT = 5
    }

    override val defaultListingOffset: Int = DEFAULT_STARTING_OFFSET

    override val defaultListingLimit: Int = DEFAULT_LISTING_LIMIT

    @Throws(
        FileNotFoundException::class,
        NotADirException::class,
        CannotReadException::class,
        OtherDirListingException::class,
        IOException::class
    )
    override fun listDir(
        path: String,
        sortingMode: SimpleSortingMode,
        reverseOrder: Boolean,
        foldersFirst: Boolean,
        dirMode: Boolean,
        offset: Int,
        limit: Int,
    )
            : List<FSItem>
    {
        val listingDir = File(path)

        if (!listingDir.exists())
            throw FileNotFoundException(path)

        if (!listingDir.isDirectory)
            throw NotADirException(path)

        if (!listingDir.canRead())
            throw CannotReadException(path)

        val fileNamesArray = File(path).list()

        val fileList = mutableListOf<FSItem>()

        if (null != fileNamesArray) {
            for (name: String in fileNamesArray) {
                fileList.add(
                    convertDirToDirItem(
                        SimpleFSItem(
                            File(path, name)
                        )
                    )
                )
            }
        } else {
            throw OtherDirListingException("Listing of '$path' returns null.")
        }

        return fileList
            .toList()
            .filter {
                // TODO: вынести этот фильтр в единое место
                if (dirMode) it.isDir
                else true
            }
            .sortedWith(
                FSItemSortingComparator.create(sortingMode, reverseOrder, foldersFirst)
            )
    }


    override suspend fun fileExists(path: String): Result<Boolean> = localCloudReader.fileExists(path)
}
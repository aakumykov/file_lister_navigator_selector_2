package com.github.aakumykov.file_lister_navigator_selector.recursive_dir_reader

import android.net.Uri
import com.github.aakumykov.file_lister_navigator_selector.file_lister.FileLister
import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import java.util.Date
import java.util.function.Predicate

class RecursiveDirReader(private val fileLister: FileLister<SimpleSortingMode>) {

    private val list: MutableList<FileListItem> = mutableListOf()

    @Deprecated("Используйте метод listDirRecursively()")
    @Throws(FileLister.NotADirException::class)
    fun getRecursiveList(path: String,
                         sortingMode: SimpleSortingMode = SimpleSortingMode.NAME,
                         reverseOrder: Boolean = false,
                         foldersFirst: Boolean = false,
                         dirMode: Boolean = false
    ): List<FileListItem> {
        list.add(
            FileListItem(
                isInitialItem = true,
                uri = Uri.parse(path),
                parentPath = "",
                isDir = true,
                mTime = Date().time,
                size = 0L
            )
        )

        while(hasUnlistedDirs()) {

            getUnlistedDir()?.let { currentlyListedDir: FileListItem ->

                fileLister.listDir(
                    currentlyListedDir.absolutePath,
                    sortingMode,
                    reverseOrder,
                    foldersFirst,
                    dirMode
                ).forEach { fsItem ->

                    val childItem = FileListItem(
                        isInitialItem = false,
                        name = fsItem.name,
                        absolutePath = fsItem.absolutePath,
                        parentPath = currentlyListedDir.absolutePath,
                        isDir = fsItem.isDir,
                        mTime = fsItem.mTime,
                        size = fsItem.size
                    )

                    currentlyListedDir.addChildId(childItem.id)

                    list.add(childItem)
                }

                currentlyListedDir.isListed = true
            }
        }

        list.remove(
            list.first { it.isInitialItem }
        )

        return list
    }


    @Throws(FileLister.NotADirException::class)
    fun listDirRecursively(path: String,
                           sortingMode: SimpleSortingMode = SimpleSortingMode.NAME,
                           reverseOrder: Boolean = false,
                           foldersFirst: Boolean = false,
                           dirMode: Boolean = false
    ): List<FileListItem> {
        return getRecursiveList(
            path = path,
             sortingMode = sortingMode,
            reverseOrder = reverseOrder,
            foldersFirst = foldersFirst,
            dirMode = dirMode
        )
    }

    private fun hasUnlistedDirs(): Boolean {
        return null != getUnlistedDir()
    }

    private fun getUnlistedDir(): FileListItem? {
        return list.firstOrNull { item ->
            val isDir = item.isDir
            val isListed = item.isListed
            isDir && !isListed
//            (item.isDirectory && !item.isListed)
        }
    }


    class FileListItem (
        /**
         * Для внутреннего использования. Служит цели удалить начальный элемент после составления списка.
         */
        val isInitialItem: Boolean,
        override val name: String,
        override val absolutePath: String,
        override val isDir: Boolean,
        override val mTime: Long,
        override val size: Long,
        override val parentPath: String,
        val childIds: MutableList<String> = mutableListOf(),
        var isListed: Boolean = false,
    )
        : FSItem
    {
        constructor(
            isInitialItem: Boolean,
            uri: Uri,
            parentPath: String,
            isDir: Boolean,
            mTime: Long,
            size: Long
        ) : this(
            isInitialItem = isInitialItem,
            name = uri.lastPathSegment!!,
            absolutePath = uri.path!!,
            parentPath = parentPath,
            isDir = isDir,
            mTime = mTime,
            size = size,
        )

        val id: String get() = absolutePath

        fun addChildId(id: String) {
            childIds.add(id)
        }

        override fun toString(): String {
            return FileListItem::class.java.simpleName + " { $name, (parentPath: '$parentPath', absolutePath: '$absolutePath') }"
        }
    }

    companion object {
        val TAG: String = RecursiveDirReader::class.java.simpleName
    }
}
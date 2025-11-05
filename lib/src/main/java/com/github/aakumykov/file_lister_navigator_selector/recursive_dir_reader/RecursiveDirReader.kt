package com.github.aakumykov.file_lister_navigator_selector.recursive_dir_reader

import android.net.Uri
import com.github.aakumykov.file_lister_navigator_selector.file_lister.FileLister
import com.github.aakumykov.file_lister_navigator_selector.file_lister.FileLister.CannotReadException
import com.github.aakumykov.file_lister_navigator_selector.file_lister.FileLister.NotADirException
import com.github.aakumykov.file_lister_navigator_selector.file_lister.FileLister.OtherDirListingException
import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode
import com.github.aakumykov.file_lister_navigator_selector.fs_item.DirItem
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class RecursiveDirReader(private val fileLister: FileLister<SimpleSortingMode>) {

    private val internalList: MutableList<FileListItem> = mutableListOf()

    // FIXME: корректно ли использовать исключения из внешней библиотеки,
    //  ведь в сигнатуре метода это выглядит некрасиво...
    //  Ответ: это создаёт "некрасивости", но стандартные библиотеки так и
    //  делают, например многие используют [java.io.FileNotFoundException],
    //  а не выдумывают своё.
    @Throws(
        FileNotFoundException::class,
        NotADirException::class,
        CannotReadException::class,
        OtherDirListingException::class,
        IOException::class
    )
    fun listDirRecursively(
        path: String,
        sortingMode: SimpleSortingMode = SimpleSortingMode.NAME,
        reverseOrder: Boolean = false,
        foldersFirst: Boolean = false,
        onlyDirsMode: Boolean = false,
        isCancelled: () -> Boolean = { false },
    )
        : List<FileListItem>
    {
        if (isCancelled.invoke())
            return emptyList()

        // Прочитываю начальный каталог.
        listDirToInternalList(
            fsItem = DirItem.fromPath(path),
            sortingMode = sortingMode,
            reverseOrder = reverseOrder,
            foldersFirst = foldersFirst,
            onlyDirsMode = onlyDirsMode,
        )

        while(hasUnlistedDirs() && !isCancelled.invoke()) {

            getUnlistedDir()?.let { currentlyListedDir: FileListItem ->

                listDirToInternalList(
                    fsItem = currentlyListedDir,
                    sortingMode = sortingMode,
                    reverseOrder = reverseOrder,
                    foldersFirst = foldersFirst,
                    onlyDirsMode = onlyDirsMode,
                )

                currentlyListedDir.isListed = true
            }
        }

        return internalList
    }


    suspend fun listDirRecursivelySuspend(
        path: String,
        sortingMode: SimpleSortingMode = SimpleSortingMode.NAME,
        reverseOrder: Boolean = false,
        foldersFirst: Boolean = false,
        dirMode: Boolean = false,
    )
        : List<FileListItem>
    {
        return suspendCancellableCoroutine { cancellableContinuation ->
            thread {
                try {
                    var isCancelled = false

                    cancellableContinuation.invokeOnCancellation {
                        isCancelled = true
                    }

                    val list = listDirRecursively(
                        path,
                        sortingMode,
                        reverseOrder,
                        foldersFirst,
                        dirMode,
                    ) { isCancelled }

                    cancellableContinuation.resume(list)

                } catch (e: Exception) {
                    cancellableContinuation.resumeWithException(e)
                }
            }
        }
    }


    private fun listDirToInternalList(
        fsItem: FSItem,
        sortingMode: SimpleSortingMode,
        foldersFirst: Boolean,
        onlyDirsMode: Boolean,
        reverseOrder: Boolean,
    ) {
        fileLister.listDir(
            fsItem.absolutePath,
            sortingMode,
            reverseOrder,
            foldersFirst,
            onlyDirsMode
        ).forEach { fsItem ->

            val childItem = FileListItem(
                isInitialItem = false,
                name = fsItem.name,
                absolutePath = fsItem.absolutePath,
                parentPath = fsItem.parentPath,
                isDir = fsItem.isDir,
                mTime = fsItem.mTime,
                size = fsItem.size
            )

            internalList.add(childItem)
        }
    }


    private fun hasUnlistedDirs(): Boolean {
        return null != getUnlistedDir()
    }

    private fun getUnlistedDir(): FileListItem? {
        return internalList.firstOrNull { item ->
            val isDir = item.isDir
            val isListed = item.isListed
            isDir && !isListed
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
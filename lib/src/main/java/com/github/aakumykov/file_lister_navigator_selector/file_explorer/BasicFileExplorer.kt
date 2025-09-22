package com.github.aakumykov.file_lister_navigator_selector.file_explorer

import com.github.aakumykov.file_lister_navigator_selector.dir_creator.DirCreator
import com.github.aakumykov.file_lister_navigator_selector.file_lister.FileLister
import com.github.aakumykov.file_lister_navigator_selector.fs_item.DirItem
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.file_lister_navigator_selector.fs_item.ParentDirItem

// FIXME: перенести кеш в реализацию
abstract class BasicFileExplorer<SortingModeType> (
    private val fileLister: FileLister<SortingModeType>,
    private val dirCreator: DirCreator,
    private val initialPath: String,
    private var isDirMode: Boolean,
    private val initialSortingMode: SortingModeType,
    private var reverseOrder: Boolean = false,
    private var foldersFirst: Boolean = true, // TODO: вынести в более явное место?
    private var listCache: FileExplorer.ListCache?, // TODO: сделать val
    private var pathCache: FileExplorer.PathCache?, // TODO: сделать val
    private val dirSeparator: String = FSItem.DS
)
    : FileExplorer<SortingModeType>
{
    private var currentPath: String = initialPath
    private var currentDir: DirItem = DirItem.fromPath(initialPath)

    private var currentSortingMode: SortingModeType = initialSortingMode

    private val currentList: MutableList<FSItem> = mutableListOf()

    override fun getCurrentPath(): String = currentPath
    override fun getCurrentDir(): DirItem = currentDir

    abstract override fun getHomeDir(): DirItem

    override fun setSortingMode(sortingMode: SortingModeType) {
        currentSortingMode = sortingMode
    }

    override fun getSortingMode(): SortingModeType = currentSortingMode

    override fun changeSortingMode(newSortingMode: SortingModeType) {
        reverseOrder = if (currentSortingMode == newSortingMode) !reverseOrder else false
        currentSortingMode = newSortingMode
    }

    override fun listCurrentPath(): List<FSItem> {

        val rawDirList = fileLister.listDir(
            currentPath,
            currentSortingMode,
            reverseOrder,
            foldersFirst,
            isDirMode
        )

        currentList.clear()
        currentList.add(ParentDirItem())

        if (isDirMode) {
            val filteredList = rawDirList.filter { fsItem -> fsItem.isDir }
            currentList.addAll(filteredList)
        } else {
            currentList.addAll(rawDirList)
        }

        listCache?.cacheList(currentList)

        return currentList
    }

    override fun listCurrentPath(offset: Int, limit: Int): List<FSItem> {

    }

    override suspend fun createDir(dirName: String): Result<String> {
        return try {
            dirCreator.makeDir(dirName)
            Result.success(dirName)
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun changeDir(dirItem: DirItem) {
        when (dirItem) {
            is ParentDirItem -> goToParentDir()
            else -> goToChildDir(dirItem.absolutePath)
        }
    }

    override fun setReverseOrder(b: Boolean) {
        reverseOrder = b
    }

    override fun getReverseOrder(): Boolean {
        return reverseOrder
    }

    override fun setFoldersFirst(b: Boolean) {
        foldersFirst = b
    }

    override fun getFoldersFirst(): Boolean {
        return foldersFirst
    }

    override fun setIsDirMode(b: Boolean) {
        this.isDirMode = b
    }

    override fun getIsDirMode(): Boolean {
        return isDirMode
    }

    private fun goToParentDir() {
        val pathParts = currentPath.split(dirSeparator)
        val parentPathParts = pathParts.subList(0, pathParts.size-1)
        var parentPath = parentPathParts.joinToString(separator = dirSeparator)
        if (parentPath.isEmpty())
            parentPath = initialPath

        changeCurrentPath(parentPath)
    }

    private fun goToChildDir(dirPath: String) {
        changeCurrentPath(dirPath)
    }

    override fun setPathCache(pathCache: FileExplorer.PathCache) {
        this.pathCache = pathCache
    }

    override fun setListCache(listCache: FileExplorer.ListCache) {
        this.listCache = listCache
    }


    private fun changeCurrentPath(path: String) {
        currentPath = path
        pathCache?.cachePath(currentPath)

        currentDir = DirItem.fromPath(path)
    }
}
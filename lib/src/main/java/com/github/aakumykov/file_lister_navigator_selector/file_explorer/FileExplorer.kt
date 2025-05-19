package com.github.aakumykov.file_lister_navigator_selector.file_explorer

import com.github.aakumykov.file_lister_navigator_selector.fs_item.DirItem
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem

/**
 * Предназначение - осуществлять навигацию по файловой системе.
 * Для этого реализации класса (ожидаемо) хранят состояние: начальный каталог (считающийся корневым)
 * и текущий путь.
 */
interface FileExplorer<SortingModeType> {

    fun changeDir(dirItem: DirItem) // TODO: выброс исключений...

    // TODO: suspend
    fun listCurrentPath(): List<FSItem> // TODO: throws NotADirException

    @Deprecated("Не используется / задействовать?")
    suspend fun createDir(dirName: String): Result<String>

    fun getCurrentPath(): String
    fun getCurrentDir(): DirItem
    fun getHomeDir(): DirItem

    fun setSortingMode(sortingMode: SortingModeType)
    fun getSortingMode(): SortingModeType

    /**
     * TODO: документировать
     */
    fun changeSortingMode(newSortingMode: SortingModeType)

    fun setReverseOrder(b: Boolean)
    fun getReverseOrder(): Boolean

    fun setFoldersFirst(b: Boolean)
    fun getFoldersFirst(): Boolean

    fun setIsDirMode(b: Boolean)
    fun getIsDirMode(): Boolean

    fun setPathCache(pathCache: PathCache)

    fun setListCache(listCache: ListCache)


    interface PathCache {
        fun cachePath(path: String)
    }

    interface ListCache {
        fun cacheList(list: List<FSItem>)
    }
}
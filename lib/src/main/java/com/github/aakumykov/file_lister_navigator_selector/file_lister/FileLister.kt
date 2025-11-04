package com.github.aakumykov.file_lister_navigator_selector.file_lister

import com.github.aakumykov.file_lister_navigator_selector.fs_item.DirItem
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import java.io.FileNotFoundException
import java.io.IOException

/**
 * Задача реализаций этого интерфейса просто выдавать список файлов по указанному пути.
 * Они не должны хранить состояние.
 */
interface FileLister<SortingModeType> {

    val defaultListingOffset: Int

    val defaultListingLimit: Int

    /**
     * Возвращает список файлов (объектов с интерфейсом [FSItem]) по указанному пути.
     *
     * @throws CannotReadException при ошибке чтения каталога.
     * @throws FileNotFoundException, если читаемый каталог отсутствует.
     * @throws NotADirException при попытке получить листинг файла.
     * @throws IOException в некоторых других случаях, зависящих от реализации.
     *
     * Обязан возвращать объекты DirItem() для каталогов и SimpleFSItem для файлов.
     * Для этого можно прогонять первичный список элементов через метод [convertDirToDirItem].
     *
     * Ответственность реализаций использовать [SortingModeType] по своему усмотрению:
     * например, LocalFileLister на его основе создаёт компаратор, который сортирует полученный список,
     * YandexDiskFileLister преобразует [SortingModeType] в строковый аргумент типа сортировки для запроса
     * к облаку.
     */
    @Throws(
        FileNotFoundException::class,
        NotADirException::class,
        CannotReadException::class,
        OtherDirListingException::class,
        IOException::class
    )
    fun listDir(
        path: String,
        sortingMode: SortingModeType,
        reverseOrder: Boolean,
        foldersFirst: Boolean,
        dirMode: Boolean,
        offset: Int = defaultListingOffset,
        limit: Int = defaultListingLimit
    ): List<FSItem>


    suspend fun fileExists(path: String): Result<Boolean>


    class NotADirException(path: String) : IOException(path)
    class CannotReadException(path: String) : IOException(path)
    class OtherDirListingException(message: String) : IOException(message)

    /**
     * Преобразует элементы типа "каталог" в объекты DirItem,
     * не-каталоги возвращает, не преобразовывая.
     */
    fun convertDirToDirItem(fsItem: FSItem): FSItem {
        return if (fsItem.isDir) DirItem(fsItem) else fsItem
    }
}

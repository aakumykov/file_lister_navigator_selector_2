package com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_dir_creator

import com.github.aakumykov.file_lister_navigator_selector.dir_creator.DirCreator
import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.yandex_disk_client.YandexDiskClient
import com.github.aakumykov.yandex_disk_client.exceptions.OperationFailedException

class YandexDiskDirCreator(private val yandexDiskClient: YandexDiskClient<FSItem, SimpleSortingMode>) : DirCreator {

    override fun makeDir(absoluteDirPath: String) {
        try {
            yandexDiskClient.createDir(absoluteDirPath)
        }
        catch (e: Exception) {
            // FIXME: убрать "!!", обновить библиотеку yandex_disk_client
            throw DirCreator.UnsuccessfulOperationException(e.message!!)
        }
    }
}
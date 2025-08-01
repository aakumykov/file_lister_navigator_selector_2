package com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_dir_creator

import com.github.aakumykov.file_lister_navigator_selector.dir_creator.DirCreator
import com.github.aakumykov.file_lister_navigator_selector.extensions.errorMsg
import com.github.aakumykov.file_lister_navigator_selector.fs_item.utils.parentPathFor
import com.github.aakumykov.yandex_disk_cloud_writer.YandexDiskCloudWriter
import java.io.File

class YandexDiskDirCreator(private val yandexDiskCloudWriter: YandexDiskCloudWriter) : DirCreator {

    override fun makeDir(absoluteDirPath: String) {
        try {
            yandexDiskCloudWriter.createDir(
                basePath = parentPathFor(absoluteDirPath),
                dirName = File(absoluteDirPath).name
            )
        }
        catch (e: Exception) {
            throw DirCreator.UnsuccessfulOperationException(e.errorMsg)
        }
    }
}
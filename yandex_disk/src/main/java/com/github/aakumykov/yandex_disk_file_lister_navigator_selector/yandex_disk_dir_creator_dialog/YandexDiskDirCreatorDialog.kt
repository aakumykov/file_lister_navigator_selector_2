package com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_dir_creator_dialog

import androidx.core.os.bundleOf
import com.github.aakumykov.file_lister_navigator_selector.dir_creator.DirCreator
import com.github.aakumykov.file_lister_navigator_selector.dir_creator_dialog.DirCreatorDialog
import com.github.aakumykov.file_lister_navigator_selector.file_selector.FileSelector.Companion.AUTH_TOKEN
import com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_dir_creator.YandexDiskDirCreator
import com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_file_lister.FileListerYandexDiskClient

class YandexDiskDirCreatorDialog : DirCreatorDialog() {

    override fun dirCreator(): DirCreator {
        val yandexDiskClient = FileListerYandexDiskClient(authToken()!!)
        return YandexDiskDirCreator(yandexDiskClient)
    }

    private fun authToken(): String? = arguments?.getString(AUTH_TOKEN)

    companion object {
        fun create(basePath: String, authToken: String): YandexDiskDirCreatorDialog {
            return YandexDiskDirCreatorDialog().apply {
                arguments = bundleOf(
                    BASE_PATH to basePath,
                    AUTH_TOKEN to authToken
                )
            }
        }
    }
}
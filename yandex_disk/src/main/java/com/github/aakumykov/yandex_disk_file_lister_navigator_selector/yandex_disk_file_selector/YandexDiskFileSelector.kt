package com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_file_selector

import androidx.core.os.bundleOf
import com.github.aakumykov.file_lister_navigator_selector.dir_creator_dialog.DirCreatorDialog
import com.github.aakumykov.file_lister_navigator_selector.file_explorer.FileExplorer
import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode
import com.github.aakumykov.file_lister_navigator_selector.file_selector.FileSelector
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.file_lister_navigator_selector.sorting_info_supplier.SimpleSortingInfoSupplier
import com.github.aakumykov.file_lister_navigator_selector.sorting_info_supplier.SortingInfoSupplier
import com.github.aakumykov.file_lister_navigator_selector.sorting_mode_translator.SimpleSortingModeTranslator
import com.github.aakumykov.file_lister_navigator_selector.sorting_mode_translator.SortingModeTranslator
import com.github.aakumykov.storage_lister.DummyStorageDirectory
import com.github.aakumykov.storage_lister.StorageDirectory
import com.github.aakumykov.yandex_disk_cloud_writer.YandexDiskCloudWriter
import com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_dir_creator.YandexDiskDirCreator
import com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_dir_creator_dialog.YandexDiskDirCreatorDialog
import com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_file_lister.YandexDiskFileLister
import com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_fs_navigator.YandexDiskFileExplorer

class YandexDiskFileSelector : FileSelector<SimpleSortingMode>()
{
    companion object {
        const val AUTH_TOKEN = "AUTH_TOKEN"

        fun create(
            authToken: String,
            initialPath: String = FSItem.ROOT_PATH,
            isDirSelectionMode: Boolean = false,
            isMultipleSelectionMode: Boolean = false
        )
                : YandexDiskFileSelector
        {
            return YandexDiskFileSelector().apply {
                arguments = bundleOf(
                    AUTH_TOKEN to authToken,
                    INITIAL_PATH to initialPath,
                    DIR_SELECTION_MODE to isDirSelectionMode,
                    MULTIPLE_SELECTION_MODE to isMultipleSelectionMode
                )
            }
        }
    }

    private var _fileExplorer: FileExplorer<SimpleSortingMode>? = null

    override fun createDirCreatorDialog(basePath: String): DirCreatorDialog {
        // TODO: как быть с "!!" ?
        return YandexDiskDirCreatorDialog.create(basePath, authToken)
    }

    override fun createSortingInfoSupplier(): SortingInfoSupplier<SimpleSortingMode> {
        return SimpleSortingInfoSupplier()
    }

    override fun createSortingModeTranslator(): SortingModeTranslator<SimpleSortingMode> {
        return SimpleSortingModeTranslator(resources)
    }

    override fun defaultSortingMode(): SimpleSortingMode {
        return SimpleSortingMode.NAME
    }

    override fun defaultReverseMode(): Boolean = false

    override fun initialStorageDirectory(): StorageDirectory {
        return DummyStorageDirectory()
    }

    override fun getDefaultInitialPath(): String = FSItem.ROOT_PATH

    override fun getDefaultDirSelectionMode(): Boolean = false

    override fun getDefaultMultipleSelectionMode(): Boolean = false


    override fun createFileExplorer(): FileExplorer<SimpleSortingMode> {
        if (null == _fileExplorer) {

            val cloudWriter = YandexDiskCloudWriter(authToken)
            val lister = YandexDiskFileLister(authToken)
            val dirCreator = YandexDiskDirCreator(cloudWriter)

            _fileExplorer = YandexDiskFileExplorer(
                initialPath = initialPath,
                yandexDiskFileLister = lister,
                yandexDiskDirCreator = dirCreator
            )
        }

        return _fileExplorer!!
    }

    override fun requestWriteAccess(
        onWriteAccessGranted: () -> Unit,
        onWriteAccessRejected: (errorMsg: String?) -> Unit
    ) {
        onWriteAccessGranted()
    }


    private val authToken: String get() = arguments?.getString(AUTH_TOKEN)!!
}
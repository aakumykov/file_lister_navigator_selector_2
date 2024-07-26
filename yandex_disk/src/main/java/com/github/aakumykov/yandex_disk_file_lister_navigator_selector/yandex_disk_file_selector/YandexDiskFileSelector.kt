package com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_file_selector

import androidx.core.os.bundleOf
import com.github.aakumykov.file_lister_navigator_selector.dir_creator_dialog.DirCreatorDialog
import com.github.aakumykov.file_lister_navigator_selector.file_explorer.FileExplorer
import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode
import com.github.aakumykov.file_lister_navigator_selector.file_selector.FileSelector
import com.github.aakumykov.file_lister_navigator_selector.sorting_info_supplier.SimpleSortingInfoSupplier
import com.github.aakumykov.file_lister_navigator_selector.sorting_info_supplier.SortingInfoSupplier
import com.github.aakumykov.file_lister_navigator_selector.sorting_mode_translator.SimpleSortingModeTranslator
import com.github.aakumykov.file_lister_navigator_selector.sorting_mode_translator.SortingModeTranslator
import com.github.aakumykov.file_lister_navigator_selector.storage_lister.StorageDirectoryWithIcon
import com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_dir_creator.YandexDiskDirCreator
import com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_dir_creator_dialog.YandexDiskDirCreatorDialog
import com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_file_lister.FileListerYandexDiskClient
import com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_file_lister.YandexDiskFileLister
import com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_fs_navigator.YandexDiskFileExplorer

// TODO: внедрять зависимости

class YandexDiskFileSelector : FileSelector<SimpleSortingMode>() {

    private var _fileExplorer: FileExplorer<SimpleSortingMode>? = null

    override fun createDirCreatorDialog(basePath: String): DirCreatorDialog {
        // TODO: как быть с "!!" ?
        return YandexDiskDirCreatorDialog.create(basePath, authToken()!!)
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

    override fun initialStorage(): StorageDirectoryWithIcon? = null

    override fun getDefaultInitialPath(): String = "/"

    override fun getDefaultDirSelectionMode(): Boolean = false

    override fun getDefaultMultipleSelectionMode(): Boolean = false


    override fun createFileExplorer(): FileExplorer<SimpleSortingMode> {
        if (null == _fileExplorer) {

            val authToken = authToken()

            if (authToken.isNullOrEmpty())
                throw IllegalArgumentException("Auth token is null or empty")

            val yandexDiskClient = FileListerYandexDiskClient(authToken)

            _fileExplorer = YandexDiskFileExplorer(
                    yandexDiskFileLister = YandexDiskFileLister(authToken),
                    yandexDiskDirCreator = YandexDiskDirCreator(yandexDiskClient),
                    initialPath = "/",
                    isDirMode = isDirMode(),
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


    private fun authToken(): String? = arguments?.getString(AUTH_TOKEN)


    companion object {
        fun create(
            fragmentResultKey: String,
            authToken: String,
            initialPath: String? = "/",
            isDirSelectionMode: Boolean = false,
            isMultipleSelectionMode: Boolean = false
        ) : YandexDiskFileSelector {
            return YandexDiskFileSelector().apply {
                arguments = bundleOf(
                    FRAGMENT_RESULT_KEY to fragmentResultKey,
                    AUTH_TOKEN to authToken,
                    INITIAL_PATH to initialPath,
                    DIR_SELECTION_MODE to isDirSelectionMode,
                    MULTIPLE_SELECTION_MODE to isMultipleSelectionMode
                )
            }
        }
    }
}
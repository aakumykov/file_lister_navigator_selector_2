package com.github.aakumykov.local_file_lister_navigator_selector.local_file_selector

import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.core.os.bundleOf
import com.github.aakumykov.android_storage_lister.AndroidStorageType
import com.github.aakumykov.file_lister_navigator_selector.dir_creator_dialog.DirCreatorDialog
import com.github.aakumykov.file_lister_navigator_selector.file_explorer.FileExplorer
import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode
import com.github.aakumykov.file_lister_navigator_selector.file_selector.FileSelector
import com.github.aakumykov.local_file_lister_navigator_selector.local_dir_creator.LocalDirCreator
import com.github.aakumykov.local_file_lister_navigator_selector.local_dir_creator_dialog.LocalDirCreatorDialog
import com.github.aakumykov.local_file_lister_navigator_selector.local_file_lister.LocalFileLister
import com.github.aakumykov.local_file_lister_navigator_selector.local_fs_navigator.LocalFileExplorer
import com.github.aakumykov.file_lister_navigator_selector.sorting_info_supplier.SimpleSortingInfoSupplier
import com.github.aakumykov.file_lister_navigator_selector.sorting_info_supplier.SortingInfoSupplier
import com.github.aakumykov.file_lister_navigator_selector.sorting_mode_translator.SimpleSortingModeTranslator
import com.github.aakumykov.file_lister_navigator_selector.sorting_mode_translator.SortingModeTranslator
import com.github.aakumykov.storage_access_helper.StorageAccessHelper
import com.github.aakumykov.storage_lister.InternalStorageDirectory
import com.github.aakumykov.storage_lister.StorageDirectory
import java.io.File

class LocalFileSelector : FileSelector<SimpleSortingMode>() {

    private lateinit var storageAccessHelper: StorageAccessHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storageAccessHelper = StorageAccessHelper.create(this).apply {
            prepareForWriteAccess()
            prepareForFullAccess()
        }
    }


    override fun defaultSortingMode(): SimpleSortingMode = SimpleSortingMode.NAME

    override fun defaultReverseMode(): Boolean = false


    override fun createFileExplorer(): FileExplorer<SimpleSortingMode> {
        return LocalFileExplorer(
            localFileLister = LocalFileLister(),
            localDirCreator = LocalDirCreator(),
            initialPath = initialPath(),
            isDirMode = isDirMode(),
            defaultSortingMode = defaultSortingMode()
        )
    }

    override fun getDefaultInitialPath(): String = Environment.getExternalStorageDirectory().absolutePath

    override fun getDefaultDirSelectionMode(): Boolean = false

    override fun getDefaultMultipleSelectionMode(): Boolean = false


    override fun createDirCreatorDialog(basePath: String): DirCreatorDialog {
        return LocalDirCreatorDialog.create(basePath)
    }

    override fun requestWriteAccess(
        onWriteAccessGranted: () -> Unit,
        onWriteAccessRejected: (errorMsg: String?) -> Unit
    ) {
        storageAccessHelper.requestWriteAccess { isGranted ->
            if (isGranted) onWriteAccessGranted()
            else onWriteAccessRejected(null)
        }
    }

    override fun createSortingModeTranslator(): SortingModeTranslator<SimpleSortingMode> {
        return SimpleSortingModeTranslator(resources)
    }

    override fun createSortingInfoSupplier(): SortingInfoSupplier<SimpleSortingMode> {
        return SimpleSortingInfoSupplier()
    }

    companion object {
        fun create(
            fragmentResultKey: String,
            initialPath: String? = null,
            isDirSelectionMode: Boolean = false,
            isMultipleSelectionMode: Boolean = false
        ) : LocalFileSelector {
            return LocalFileSelector().apply {
                arguments = bundleOf(
                    FRAGMENT_RESULT_KEY to fragmentResultKey,
                    INITIAL_PATH to initialPath,
                    DIR_SELECTION_MODE to isDirSelectionMode,
                    MULTIPLE_SELECTION_MODE to isMultipleSelectionMode
                )
            }
        }
    }

    override fun initialDirectory(): StorageDirectory {
        return Environment.getExternalStorageDirectory().let {
            InternalStorageDirectory(
                name = it.name,
                path = it.absolutePath,
            )
        }
    }
}







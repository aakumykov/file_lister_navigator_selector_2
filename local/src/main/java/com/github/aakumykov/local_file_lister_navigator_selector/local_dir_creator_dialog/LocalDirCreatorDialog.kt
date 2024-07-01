package com.github.aakumykov.local_file_lister_navigator_selector.local_dir_creator_dialog

import androidx.core.os.bundleOf
import com.github.aakumykov.file_lister_navigator_selector.dir_creator.DirCreator
import com.github.aakumykov.file_lister_navigator_selector.dir_creator_dialog.DirCreatorDialog
import com.github.aakumykov.local_file_lister_navigator_selector.local_dir_creator.LocalDirCreator

class LocalDirCreatorDialog : DirCreatorDialog() {

    override fun dirCreator(): DirCreator = LocalDirCreator()

    companion object {
        fun create(basePath: String): LocalDirCreatorDialog {
            return LocalDirCreatorDialog().apply {
                arguments = bundleOf(BASE_PATH to basePath)
            }
        }
    }
}
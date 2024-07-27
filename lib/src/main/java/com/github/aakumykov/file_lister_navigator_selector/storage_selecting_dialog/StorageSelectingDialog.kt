package com.github.aakumykov.file_lister_navigator_selector.storage_selecting_dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.github.aakumykov.file_lister_navigator_selector.R
import com.github.aakumykov.storage_lister.StorageDirectory

class StorageSelectingDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val storageList = arguments?.getParcelableArrayList<StorageDirectory>(STORAGE_LIST) ?: emptyList()
        val selectedStorage = arguments?.getParcelable<StorageDirectory>(SELECTED_STORAGE)

        return AlertDialog.Builder(requireContext())
            /*.setSingleChoiceItems(
                storageArray,
                0,
                object: DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {

                    }
                }
            )*/
            .setTitle("Выбор хранилища")
            .setMessage(storageList.joinToString("\n") { it.name })
            .setNegativeButton("Отмена") { _,_ -> }
            .create()
    }

    companion object {
        const val STORAGE_LIST = "STORAGE_LIST"
        const val SELECTED_STORAGE = "SELECTED_STORAGE"
        val TAG: String = StorageSelectingDialog::class.java.simpleName

        fun create(storageList: List<StorageDirectory>, selectedStorage: StorageDirectory?): StorageSelectingDialog {
            return StorageSelectingDialog().apply {
                arguments = bundleOf(
                    STORAGE_LIST to storageList,
                    SELECTED_STORAGE to selectedStorage
                )
            }
        }
    }
}
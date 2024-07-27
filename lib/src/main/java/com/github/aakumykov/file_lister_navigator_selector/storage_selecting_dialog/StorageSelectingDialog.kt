package com.github.aakumykov.file_lister_navigator_selector.storage_selecting_dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.LifecycleOwner
import com.github.aakumykov.storage_lister.StorageDirectory

class StorageSelectingDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val storageListAdapter = StorageListAdapter()

        arguments?.getParcelableArrayList<StorageDirectory>(STORAGE_LIST)?.also { storageList ->
            storageListAdapter.setList(storageList)
        }

        arguments?.getParcelable<StorageDirectory>(SELECTED_STORAGE)?.also { selectedStorage ->
            storageListAdapter.setSelectedItem(selectedStorage)
        }

        return AlertDialog.Builder(requireContext())
            .setAdapter(storageListAdapter) { _, position ->
                setFragmentResult(STORAGE_SELECTION_RESULT, bundleOf(
                    SELECTED_STORAGE to storageListAdapter.getItem(position)
                ))
            }
            .setTitle("Выбор хранилища")
            .setNegativeButton("Отмена") { _,_ -> }
            .create()
    }

    companion object {

        const val STORAGE_LIST = "STORAGE_LIST"
        const val SELECTED_STORAGE = "SELECTED_STORAGE"
        const val STORAGE_SELECTION_RESULT = "STORAGE_SELECTION_RESULT"

        val TAG: String = StorageSelectingDialog::class.java.simpleName

        fun create(storageList: List<StorageDirectory>, selectedStorage: StorageDirectory?): StorageSelectingDialog {
            return StorageSelectingDialog().apply {
                arguments = bundleOf(
                    STORAGE_LIST to storageList,
                    SELECTED_STORAGE to selectedStorage
                )
            }
        }

        fun listenForResult(
            fragmentManager: FragmentManager,
            viewLifecycleOwner: LifecycleOwner,
            fragmentResultListener: FragmentResultListener
        ) {
            fragmentManager.setFragmentResultListener(STORAGE_SELECTION_RESULT, viewLifecycleOwner, fragmentResultListener)
        }
    }
}
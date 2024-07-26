package com.github.aakumykov.storage_selector

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import com.github.aakumykov.android_storage_lister.AndroidStorageDirectory
import com.github.aakumykov.storage_selector.StorageSelectingDialog.Companion
import com.github.aakumykov.storage_selector.StorageSelectingDialog.Companion.LIST_TO_DISPLAY
import com.github.aakumykov.storage_selector.StorageSelectingDialog.Companion.SELECTED_STORAGE
import com.github.aakumykov.storage_selector.StorageSelectingDialog.Companion.TAG

class QwertyDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val qwerty = arguments?.getString(QWERTY)
        Log.d(TAG,"qwerty=$qwerty")

        return super.onCreateDialog(savedInstanceState)
    }

    companion object {
        val TAG: String = StorageSelectingDialog::class.java.simpleName
        const val QWERTY = "QWERTY"

        fun create(): QwertyDialog {

            return QwertyDialog().apply {
                arguments = Bundle().apply {
                    QWERTY to "йцукен"
                }
            }
        }
    }
}
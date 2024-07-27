package com.github.aakumykov.storage_selector

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.LifecycleOwner
import com.github.aakumykov.android_storage_lister.AndroidStorageDirectory

class StorageSelectingDialog : DialogFragment(), AdapterView.OnItemClickListener {

    private lateinit var storageListAdapter: com.github.aakumykov.file_lister_navigator_selector.storage_selecting_dialog.StorageListAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val name = arguments?.getString(NAME)
        Log.d(TAG,"name=$name")

        // Адаптер списка
        storageListAdapter =
            com.github.aakumykov.file_lister_navigator_selector.storage_selecting_dialog.StorageListAdapter()

        /*if (null != arguments) {
//            arguments?.also {
                val qwerty = arguments?.getString(QWERTY)
                Log.d(TAG, qwerty ?: "qwerty is null")
//            } ?: {
//                Log.d(TAG, "arguments == null")
//            }
        } else {
            Log.d(TAG, "arguments == null")
        }

        // Список для отображения
        arguments?.getParcelableArrayList<StorageDirectory>(LIST_TO_DISPLAY)?.also { list ->
            list.map { storageDirectory ->
                StorageWithIcon.create(storageDirectory)
            }
                .let { it }
                .also {
                storageListAdapter.setList(it)
            }
        }

        // Предвыбранный элемент
        arguments?.getParcelable<StorageWithIcon>(SELECTED_STORAGE)?.also { selectedStorage ->
            storageListAdapter.setSelectedItem(selectedStorage)
        }*/


        // Создание разметки списка
        val listLayout = layoutInflater.inflate(R.layout.dialog_storage_selector, null)

        val listView: ListView = listLayout.findViewById(R.id.listView)
        listView.adapter = storageListAdapter
        listView.divider = ResourcesCompat.getDrawable(resources, R.drawable.list_divider, requireContext().theme)
        listView.dividerHeight = 1

        listView.onItemClickListener = this


        // Создание диалога
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_title)
            .setView(listLayout)
            .setNeutralButton(getString(R.string.cancel)) { _,_ -> }
            .create()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        val selectedStorage = storageListAdapter.getItem(position)

        storageListAdapter.setSelectedItem(selectedStorage)

        setFragmentResult(
            STORAGE_SELECTION_REQUEST, bundleOf(
            STORAGE_SELECTION_RESULT to selectedStorage
        ))

        dismiss()
    }

    companion object {

        val TAG: String = StorageSelectingDialog::class.java.simpleName

        const val LIST_TO_DISPLAY = "LIST_TO_DISPLAY"
        const val SELECTED_STORAGE = "SELECTED_STORAGE"
        const val STORAGE_SELECTION_REQUEST = "STORAGE_SELECTION_REQUEST"
        const val STORAGE_SELECTION_RESULT = "STORAGE_SELECTION_RESULT"
        const val NAME = "NAME"

        fun create(
            storageList: List<AndroidStorageDirectory>,
            selectedStorage: AndroidStorageDirectory? = null
        ): StorageSelectingDialog {

            return StorageSelectingDialog().apply {
                arguments = Bundle().apply {
                    LIST_TO_DISPLAY to storageList
                    SELECTED_STORAGE to selectedStorage
                    NAME to "Вася"
                }
            }
        }


        fun listenForResult(
            fragmentManager: FragmentManager,
            lifecycleOwner: LifecycleOwner,
            listener: FragmentResultListener
        ) {
            fragmentManager.setFragmentResultListener(STORAGE_SELECTION_REQUEST, lifecycleOwner, listener)
        }

        @DrawableRes
        fun getIconFor(androidStorageDirectory: AndroidStorageDirectory): Int {
            return StorageWithIcon.iconFor(androidStorageDirectory.type)
        }
    }
}

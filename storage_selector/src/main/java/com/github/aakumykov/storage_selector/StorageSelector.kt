package com.github.aakumykov.storage_selector

import android.app.Dialog
import android.content.Context
import android.os.Bundle
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
import com.github.aakumykov.android_storage_lister.AndroidStorageLister
import com.github.aakumykov.android_storage_lister.StorageDirectory
import com.github.aakumykov.list_holding_list_adapter.ListHoldingListAdapter
import kotlin.collections.ArrayList

class StorageSelector : DialogFragment(), AdapterView.OnItemClickListener {

    private lateinit var storageListAdapter: ListHoldingListAdapter<StorageWithIcon, StorageListViewHolder>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // Адаптер списка
        storageListAdapter = StorageListAdapter()
        storageListAdapter.setList(
            getStorageList(requireContext())
                .let { it }
                .map { androidStorageDirectory ->
                    StorageWithIcon.create(androidStorageDirectory)
                }
        )


        // Предвыбранный элемент
        arguments?.getParcelable<StorageWithIcon>(SELECTED_STORAGE)?.also { selectedStorage ->
            storageListAdapter.setSelectedItem(selectedStorage)
            storageListAdapter.notifyDataSetChanged()

        }


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

        val TAG: String = StorageSelector::class.java.simpleName

        const val LIST_TO_DISPLAY = "LIST_TO_DISPLAY"
        const val SELECTED_STORAGE = "SELECTED_STORAGE"
        const val STORAGE_SELECTION_REQUEST = "STORAGE_SELECTION_REQUEST"
        const val STORAGE_SELECTION_RESULT = "STORAGE_SELECTION_RESULT"

        fun create(listToDisplay: List<StorageWithIcon>, selectedStorage: StorageWithIcon? = null): StorageSelector {
            return createReal(listToDisplay, selectedStorage)
        }

        fun create(selectedStorage: StorageWithIcon? = null): StorageSelector {
            return createReal(selectedStorage = selectedStorage)
        }

        private fun createReal(listToDisplay: List<StorageWithIcon>? = null, selectedStorage: StorageWithIcon? = null): StorageSelector {
            return StorageSelector().apply {
                arguments = Bundle().apply {
                    if (null != listToDisplay) putParcelableArrayList(LIST_TO_DISPLAY, java.util.ArrayList(listToDisplay))
                    if (null != selectedStorage) putParcelable(SELECTED_STORAGE, selectedStorage)
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


        fun show(fragmentManager: FragmentManager) {
            create().show(fragmentManager, TAG)
        }


        fun getStorageList(context: Context): ArrayList<AndroidStorageDirectory> {
            return ArrayList(
                AndroidStorageLister(context).storageDirectories.toList()
            )
        }

        @DrawableRes
        fun getIconFor(androidStorageDirectory: AndroidStorageDirectory): Int {
            return StorageWithIcon.iconFor(androidStorageDirectory.type)
        }
    }
}

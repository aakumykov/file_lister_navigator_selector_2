package com.github.aakumykov.storage_selector

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.LifecycleOwner
import com.github.aakumykov.list_holding_list_adapter.ListHoldingListAdapter
import kotlin.collections.ArrayList

class StorageSelectionDialog : DialogFragment(), AdapterView.OnItemClickListener {

    private lateinit var storageListAdapter: ListHoldingListAdapter<Storage, StorageListViewHolder>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // Список для отображения
        arguments?.getParcelableArrayList<Storage>(LIST_TO_DISPLAY)?.also { list ->
            storageListAdapter.setList(list)
        }

        // Предвыбранный элемент
        arguments?.getParcelable<Storage>(SELECTED_STORAGE)?.also { selectedStorage ->
            storageListAdapter.setSelectedItem(selectedStorage)
            storageListAdapter.notifyDataSetChanged()

        }

        // Адаптер списка
        storageListAdapter = StorageListAdapter()

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

    companion object {

        val TAG: String = StorageSelectionDialog::class.java.simpleName

        const val LIST_TO_DISPLAY = "LIST_TO_DISPLAY"
        const val SELECTED_STORAGE = "SELECTED_STORAGE"
        const val STORAGE_SELECTION_REQUEST = "STORAGE_SELECTION_REQUEST"
        const val STORAGE_SELECTION_RESULT = "STORAGE_SELECTION_RESULT"

        fun create(listToDisplay: List<Storage>, selectedStorage: Storage? = null): StorageSelectionDialog {
            return StorageSelectionDialog().apply {
                arguments = bundleOf(
                    LIST_TO_DISPLAY to ArrayList(listToDisplay),
                    SELECTED_STORAGE to selectedStorage,
                )
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
            create(emptyList()).show(fragmentManager, TAG)
        }
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
}

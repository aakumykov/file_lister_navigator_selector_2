package com.github.aakumykov.file_lister_navigator_selector.sorting_dialog

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.github.aakumykov.file_lister_navigator_selector.R
import com.github.aakumykov.file_lister_navigator_selector.databinding.DialogSortingBinding
import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode

class SortingDialog<SortingModeType> : DialogFragment() {

//    abstract val defaultSortingMode: SortingModeType
//    abstract val defaultIsDirectOrder: Boolean
//    abstract val defaultFoldersFirst: Boolean

//    abstract fun string2sortingMode(s: String): SortingModeType
    fun viewId2sortingMode(id: Int): SortingModeType {

    }
    
    private var _binding: DialogSortingBinding? = null
    private val binding: DialogSortingBinding get() = _binding!!
    
    private var callbacks: Callbacks<SortingModeType>? = null
    
    
    private val currentSortingMode: SortingModeType 
        get() = viewId2sortingMode(binding.sortingModeSelector.checkedRadioButtonId)
    
    private val currentIsDirectOrder: Boolean
        get() = !binding.reverseOrderCheckbox.isChecked
    
    private val currentFoldersFirst: Boolean 
        get() = binding.foldersFirstCheckbox.isChecked

    
    interface Callbacks<SortingModeType> {
        fun onSortingModeChanged(newMode: SortingModeType, 
                                 isDirectOrder: Boolean,
                                 foldersFirst: Boolean)
    }
    
    fun setCallbacks(callbacks: Callbacks<SortingModeType>): SortingDialog<SortingModeType> {
        this.callbacks = callbacks
        return this
    }

    fun display(fragmentManager: FragmentManager) {
        show(fragmentManager, TAG)
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        _binding = DialogSortingBinding.inflate(layoutInflater)
        
        applyInitialModesToView()
        configButtons()

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setTitle(R.string.sorting_dialog_title)
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun configButtons() {
        view.apply {
            binding.sortingModeSelector.setOnCheckedChangeListener { _, id -> onSortingModeChanged(id) }
            binding.reverseOrderCheckbox.setOnCheckedChangeListener { _, isChecked -> onReverseOrderChanged(isChecked) }
            binding.foldersFirstCheckbox.setOnCheckedChangeListener { _, isChecked -> onFoldersFirstChanged(isChecked) }
            binding.applyButton.setOnClickListener { onApplyClicked() }
        }
    }

    private fun onSortingModeChanged(@IdRes variantId: Int) {
//        storeStringInPreferences(INITIAL_SORTING_MODE, viewId2sortingModeName(variantId))
    }

    private fun onReverseOrderChanged(isChecked: Boolean) {
//        storeBooleanInPreferences(IS_DIRECT_ORDER, !isDirectOrder)
    }

    private fun onFoldersFirstChanged(isChecked: Boolean) {
//        storeBooleanInPreferences(FOLDERS_FIRST, isChecked)
    }

    private fun applyInitialModesToView() {
        view.apply {
            binding.sortingModeSelector.check(sortingMode2viewId(initialSortingMode))
            binding.reverseOrderCheckbox.isChecked = !initialDirectOrder
            binding.foldersFirstCheckbox.isChecked = initialFoldersFirst
        }
    }

//    @IdRes
//    protected abstract fun sortingMode2viewId(sortingModeType: SortingModeType): Int

    private fun onApplyClicked() {
        callbacks?.onSortingModeChanged(
            currentSortingMode,
            currentIsDirectOrder,
            currentFoldersFirst
        )
        dismiss()
    }

    private val initialSortingMode: SortingModeType get() {
        return arguments?.getString(INITIAL_SORTING_MODE)?.let {
            string2sortingMode(it)
        } ?: run {
            defaultSortingMode
        }
    }

    private val initialDirectOrder: Boolean get() {
        return arguments?.getBoolean(IS_DIRECT_ORDER)
            ?: defaultIsDirectOrder
    }

    private val initialFoldersFirst: Boolean get() {
        return arguments?.getBoolean(FOLDERS_FIRST)
            ?: defaultFoldersFirst
    }

    companion object {
        val TAG: String = SortingDialog::class.java.simpleName

        const val INITIAL_SORTING_MODE = "INITIAL_SORTING_MODE"
        const val IS_DIRECT_ORDER = "IS_DIRECT_ORDER"
        const val FOLDERS_FIRST = "FOLDERS_FIRST"

        fun findDialog(fragmentManager: FragmentManager): Fragment? {
            return fragmentManager.findFragmentByTag(TAG)
        }

        fun <SortingModeType> create(
            callbacks: Callbacks<SortingModeType>,
            initialSortingMode: SortingModeType? = null,
            isDirectOrder: Boolean? = true,
            foldersFirst: Boolean? = true,
        ): SortingDialog<SortingModeType> {
            return SortingDialog<SortingModeType>().apply {
                arguments = bundleOf(
                    INITIAL_SORTING_MODE to initialSortingMode,
                    IS_DIRECT_ORDER to isDirectOrder,
                    FOLDERS_FIRST to foldersFirst,
                )
                setCallbacks(callbacks)
            }
        }

        fun <SortingModeType> reconnectToDialog(
            fragmentManager: FragmentManager,
            callbacks: Callbacks<SortingModeType>
        ) {
            findDialog(fragmentManager)?.also {
                (it as SortingDialog<SortingModeType>).setCallbacks(callbacks)
            }

        }
    }
}
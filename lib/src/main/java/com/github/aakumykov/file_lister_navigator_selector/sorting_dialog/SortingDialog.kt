package com.github.aakumykov.file_lister_navigator_selector.sorting_dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.github.aakumykov.file_lister_navigator_selector.R
import com.github.aakumykov.file_lister_navigator_selector.databinding.DialogSortingBinding
import com.github.aakumykov.file_lister_navigator_selector.sorting_mode_translator.SortingModeTranslator

class SortingDialog<SortingModeType> : DialogFragment() {

    private var defaultSortingMode: SortingModeType? = null
    private var defaultIsDirectOrder: Boolean? = null
    private var defaultFoldersFirst: Boolean? = null

    private var _binding: DialogSortingBinding? = null
    private val binding: DialogSortingBinding get() = _binding!!
    
    private var callbacks: Callbacks<SortingModeType>? = null

    private var _sortingModeTranslator: SortingModeTranslator<SortingModeType>? = null
    private val sortingModeTranslator: SortingModeTranslator<SortingModeType> get() = _sortingModeTranslator!!

    private val currentSortingMode: SortingModeType 
        get() = sortingModeTranslator.viewId2sortingMode(binding.sortingModeSelector.checkedRadioButtonId)
            ?: initialSortingMode
    
    private val currentIsDirectOrder: Boolean
        get() = !binding.reverseOrderCheckbox.isChecked
    
    private val currentFoldersFirst: Boolean 
        get() = binding.foldersFirstCheckbox.isChecked

    
    interface Callbacks<SortingModeType> {
        fun onSortingModeChanged(newMode: SortingModeType, 
                                 isDirectOrder: Boolean, foldersFirst: Boolean)
    }

    fun init(
        callbacks: Callbacks<SortingModeType>,
        translator: SortingModeTranslator<SortingModeType>
    ): SortingDialog<SortingModeType>
    {
        setCallbacks(callbacks)
        setSortingModeTranslator(translator)
        applyInitialModesToView()
        return this
    }

    private fun setCallbacks(callbacks: Callbacks<SortingModeType>): SortingDialog<SortingModeType> {
        this.callbacks = callbacks
        return this
    }

    private fun setSortingModeTranslator(translator: SortingModeTranslator<SortingModeType>) {
        _sortingModeTranslator = translator
    }

    private fun setDefaultModes(defaultSortingMode: SortingModeType,
                        defaultIsDirectOrder: Boolean,
                        defaultFoldersFirst: Boolean) {
        this.defaultSortingMode = defaultSortingMode
        this.defaultIsDirectOrder = defaultIsDirectOrder
        this.defaultFoldersFirst = defaultFoldersFirst
    }

    fun display(
        fragmentManager: FragmentManager,
        callbacks: Callbacks<SortingModeType>,
        translator: SortingModeTranslator<SortingModeType>
    ) {
        this.callbacks = callbacks
        _sortingModeTranslator = translator
        show(fragmentManager, TAG)
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        _binding = DialogSortingBinding.inflate(layoutInflater)

        configButtons()

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setTitle(R.string.sorting_dialog_title)
            .create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(callbacks!!, sortingModeTranslator)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun configButtons() {
        binding.applyButton.setOnClickListener { onApplyClicked() }
    }

    private fun applyInitialModesToView() {
        view.apply {
            binding.sortingModeSelector.check(sortingModeTranslator.sortingMode2viewId(initialSortingMode))
            binding.reverseOrderCheckbox.isChecked = !initialDirectOrder
            binding.foldersFirstCheckbox.isChecked = initialFoldersFirst
        }
    }

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
            sortingModeTranslator.string2sortingMode(it)
        } ?: defaultSortingMode!!
    }

    private val initialDirectOrder: Boolean get() {
        return arguments?.getBoolean(IS_DIRECT_ORDER)
            ?: defaultIsDirectOrder!!
    }

    private val initialFoldersFirst: Boolean get() {
        return arguments?.getBoolean(FOLDERS_FIRST)
            ?: defaultFoldersFirst!!
    }

    companion object {
        val TAG: String = SortingDialog::class.java.simpleName

        const val INITIAL_SORTING_MODE = "INITIAL_SORTING_MODE"
        const val IS_DIRECT_ORDER = "IS_DIRECT_ORDER"
        const val FOLDERS_FIRST = "FOLDERS_FIRST"

        fun <SortingModeType> create(
            sortingMode: SortingModeType,
            isDirectOrder: Boolean = true,
            foldersFirst: Boolean = true,
            callbacks: Callbacks<SortingModeType>,
            translator: SortingModeTranslator<SortingModeType>
        )
        : SortingDialog<SortingModeType>
        {
            return SortingDialog<SortingModeType>().apply {

                setDefaultModes(
                    defaultSortingMode = sortingMode,
                    defaultIsDirectOrder = isDirectOrder,
                    defaultFoldersFirst = foldersFirst
                )

                this.callbacks = callbacks
                _sortingModeTranslator = translator

                arguments = bundleOf(
                    INITIAL_SORTING_MODE to sortingMode,
                    IS_DIRECT_ORDER to isDirectOrder,
                    FOLDERS_FIRST to foldersFirst,
                )
            }
        }

        fun <SortingModeType> reconnectToDialog(
            fragmentManager: FragmentManager,
            callbacks: Callbacks<SortingModeType>,
            translator: SortingModeTranslator<SortingModeType>,
            defaultSortingMode: SortingModeType,
            defaultIsDirectOrder: Boolean,
            defaultFoldersFirst: Boolean
        )
            : SortingDialog<SortingModeType>?
        {
            return fragmentManager.findFragmentByTag(TAG)?.let {
                (it as SortingDialog<SortingModeType>).apply {
                    setDefaultModes(
                        defaultSortingMode,
                        defaultIsDirectOrder,
                        defaultFoldersFirst
                    )
                    init(callbacks, translator)
                }
            }
        }
    }
}
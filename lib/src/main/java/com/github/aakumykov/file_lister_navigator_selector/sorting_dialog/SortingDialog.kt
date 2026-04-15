package com.github.aakumykov.file_lister_navigator_selector.sorting_dialog

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.marginStart
import androidx.core.view.setMargins
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.github.aakumykov.file_lister_navigator_selector.R
import com.github.aakumykov.file_lister_navigator_selector.extensions.errorMsg
import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode

abstract class SortingDialog<SortingModeType> : DialogFragment() {

    abstract val defaultSortingMode: SortingModeType
    abstract val defaultIsDirectOrder: Boolean
    abstract val defaultFoldersFirst: Boolean

    abstract fun string2sortingMode(s: String): SortingModeType
    abstract val sortingModesMap: Map<Int, SortingModeType>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view: View = prepareView()

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setTitle(R.string.sorting_dialog_title)
            .setPositiveButton(R.string.dialog_button_apply) { _,_ -> onApplyClicked() }
            .setNegativeButton(R.string.dialog_button_cancel) { _,_ -> }
            .create()
    }

    private fun prepareView(): View {
        val layout = layoutInflater.inflate(R.layout.dialog_sorting, null)
        val rootView = layout.findViewById<ViewGroup>(R.id.sortingDialogRootView)
        generateSortingModes(rootView)
        return layout
    }

    private fun generateSortingModes(viewGroup: ViewGroup)/*: View*/ {
        RadioGroup(viewGroup.context).apply {
            val radioGroup = this
            layoutParams = RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT)
                .apply {
                    val marginH = resources.getInteger(R.integer.sorting_dialog_content_horizontal_margin)
                    val marginV = resources.getInteger(R.integer.sorting_dialog_content_vertical_margin)
                    setMargins(
                        marginH,
                        marginV,
                        marginH,
                        marginV,
                    )
                }

            sortingModesMap.forEach { nameRes, sortingMode ->
                val radioButton = RadioButton(viewGroup.context).apply {
                    layoutParams = ViewGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT)
                    setText(nameRes)
                    isChecked = sortingMode == initialSortingMode
                }
                radioGroup.addView(radioButton)
            }
            viewGroup.addView(radioGroup)
        }
    }

    fun display(fragmentManager: FragmentManager) {
        show(fragmentManager, TAG)
    }

    private fun onApplyClicked() {

    }

    private val initialSortingMode: SortingModeType get() {
        return arguments?.getString(INITIAL_SORTING_MODE)?.let {
            string2sortingMode(it)
        } ?: defaultSortingMode
    }

    private val isDirectOrder: Boolean get() {
        return arguments?.getBoolean(IS_DIRECT_ORDER)
            ?: defaultIsDirectOrder
    }

    private val foldersFirst: Boolean get() {
        return arguments?.getBoolean(FOLDERS_FIRST)
            ?: defaultFoldersFirst
    }

    companion object {
        val TAG: String = SortingDialog::class.java.simpleName
        const val INITIAL_SORTING_MODE = "INITIAL_SORTING_MODE"
        const val IS_DIRECT_ORDER = "IS_DIRECT_ORDER"
        const val FOLDERS_FIRST = "FOLDERS_FIRST"
    }
}

class SimpleSortingDialog(
    override val defaultSortingMode: SimpleSortingMode = SimpleSortingMode.NAME,
    override val defaultIsDirectOrder: Boolean = true,
    override val defaultFoldersFirst: Boolean = true,
)
    : SortingDialog<SimpleSortingMode>()
{
    override fun string2sortingMode(s: String): SimpleSortingMode {
        return try { SimpleSortingMode.valueOf(s) }
        catch (t: Throwable) {
            Log.e(TAG, t.errorMsg, t)
            defaultSortingMode
        }
    }

    override val sortingModesMap: Map<Int, SimpleSortingMode>
        get() = mapOf(
            R.string.sorting_mode_name to SimpleSortingMode.NAME,
            R.string.sorting_mode_size to SimpleSortingMode.SIZE,
            R.string.sorting_mode_c_time to SimpleSortingMode.C_TIME,
            R.string.sorting_mode_m_time to SimpleSortingMode.M_TIME,
        )

    companion object {
        val TAG: String = SimpleSortingDialog::class.java.simpleName

        fun create(
            initialSortingMode: SimpleSortingMode = SimpleSortingMode.NAME,
            isDirectOrder: Boolean = true,
            foldersFirst: Boolean = true
        ): SimpleSortingDialog {
            return SimpleSortingDialog().apply {
                INITIAL_SORTING_MODE to initialSortingMode
                IS_DIRECT_ORDER to isDirectOrder
                FOLDERS_FIRST to foldersFirst
            }
        }
    }
}
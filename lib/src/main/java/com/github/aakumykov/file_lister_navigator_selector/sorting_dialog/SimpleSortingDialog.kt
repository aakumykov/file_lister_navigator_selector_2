package com.github.aakumykov.file_lister_navigator_selector.sorting_dialog

import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import com.github.aakumykov.file_lister_navigator_selector.R
import com.github.aakumykov.file_lister_navigator_selector.extensions.errorMsg
import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode

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

    override fun viewId2sortingMode(id: Int): SimpleSortingMode = when(id) {
        R.id.sortingModeBySize -> SimpleSortingMode.SIZE
        R.id.sortingModeByCTime -> SimpleSortingMode.C_TIME
        R.id.sortingModeByMTime -> SimpleSortingMode.M_TIME
        else -> SimpleSortingMode.NAME
    }

    override fun sortingMode2viewId(sortingModeType: SimpleSortingMode): Int {
         return when(sortingModeType) {
            SimpleSortingMode.SIZE -> R.id.sortingModeBySize
            SimpleSortingMode.C_TIME -> R.id.sortingModeByCTime
            SimpleSortingMode.M_TIME -> R.id.sortingModeByMTime
            else -> R.id.sortingModeByName
        }
    }

    companion object {
        val TAG: String = SimpleSortingDialog::class.java.simpleName

        fun create(
            callbacks: Callbacks<SimpleSortingMode>,
            initialSortingMode: SimpleSortingMode? = SimpleSortingMode.NAME,
            isDirectOrder: Boolean? = true,
            foldersFirst: Boolean? = true,
        ): SimpleSortingDialog {
            return SimpleSortingDialog().apply {
                arguments = bundleOf(
                    INITIAL_SORTING_MODE to initialSortingMode?.name,
                    IS_DIRECT_ORDER to isDirectOrder,
                    FOLDERS_FIRST to foldersFirst,
                )
                setCallbacks(callbacks)
            }
        }

        fun reconnectToDialog(
            fragmentManager: FragmentManager,
            callbacks: Callbacks<SimpleSortingMode>
        ) {
            findDialog(fragmentManager)?.also {
                (it as SimpleSortingDialog).setCallbacks(callbacks)
            }

        }
    }
}
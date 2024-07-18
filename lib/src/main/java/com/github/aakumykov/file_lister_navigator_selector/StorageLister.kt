package com.github.aakumykov.file_lister_navigator_selector

import android.content.Context
import android.os.Environment
import androidx.annotation.StringRes
import com.github.aakumykov.file_lister_navigator_selector.entities.Storage
import com.github.aakumykov.file_lister_navigator_selector.utils.ExternalSDCardDetector

class StorageLister(
    private val applicationContext: Context,
    @StringRes private val internalStorageTitle: Int,
    @StringRes private val externalStorageTitle: Int,
) {
    fun listStorages(): List<Storage> {
        return mutableListOf(
            Storage(
                applicationContext.getString(internalStorageTitle),
                Environment.getExternalStorageDirectory().absolutePath,
            ),
            ExternalSDCardDetector.getExternalCardDirectory(applicationContext)?.let { path ->
                Storage(
                    applicationContext.getString(externalStorageTitle),
                    path
                )
            }
        ).filterNotNull()
    }
}
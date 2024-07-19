package com.github.aakumykov.file_lister_navigator_selector.utils

import android.content.Context
import android.os.Environment
import androidx.annotation.StringRes
import com.github.aakumykov.file_lister_navigator_selector.R
import com.github.aakumykov.file_lister_navigator_selector.entities.Storage

class StorageDetector(
    @StringRes private val internalStorageTitle: Int = R.string.internal_storage_title,
    @StringRes private val externalStorageTitle: Int = R.string.external_storage_title,
) {

    fun listDeviceStorages(context: Context): List<Storage> {
        return mutableListOf<Storage>().apply {

            // Внутреннее хранилище
            add(Storage(
                R.drawable.ic_storage_phone_drop_down,
                context.getString(internalStorageTitle),
                Environment.getExternalStorageDirectory().absolutePath
            ))

            // SD-карта (если есть)
            ExternalSDCardDetector.getExternalCardDirectory(context)?.also { sdCardPath ->
                add(Storage(
                    R.drawable.ic_storage_sd_card_selected,
                    context.getString(externalStorageTitle),
                    sdCardPath
                ))
            }

        }
    }
}

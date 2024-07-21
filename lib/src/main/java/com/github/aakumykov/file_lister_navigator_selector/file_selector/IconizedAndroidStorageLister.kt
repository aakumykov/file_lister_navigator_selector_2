package com.github.aakumykov.file_lister_navigator_selector.file_selector

import android.content.Context
import com.github.aakumykov.android_storage_lister.AndroidStorageLister
import com.github.aakumykov.android_storage_lister.AndroidStorageType
import com.github.aakumykov.android_storage_lister.StorageDirectory
import com.github.aakumykov.file_lister_navigator_selector.R
import com.github.aakumykov.file_lister_navigator_selector.entities.Storage

class IconizedAndroidStorageLister(context: Context) : AndroidStorageLister<Storage>(context) {

    override fun createStorageRepresentationObject(storageDirectory: StorageDirectory?): Storage? {

        if (null == storageDirectory)
            return null

        val icon = when(storageDirectory.type) {
            AndroidStorageType.USB -> R.drawable.ic_storage_usb
            AndroidStorageType.INTERNAL -> R.drawable.ic_storage_internal
            AndroidStorageType.SD_CARD -> R.drawable.ic_storage_sd_card
        }

        return Storage(
            path = storageDirectory.path,
            icon = icon,
            name = storageDirectory.name
        )
    }
}

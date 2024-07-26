package com.github.aakumykov.file_lister_navigator_selector.utils

import android.content.Context
import com.github.aakumykov.android_storage_lister.AndroidStorageDirectory
import com.github.aakumykov.android_storage_lister.AndroidStorageLister
import com.github.aakumykov.android_storage_lister.AndroidStorageType
import com.github.aakumykov.file_lister_navigator_selector.R
import com.github.aakumykov.file_lister_navigator_selector.storage_lister.StorageDirectoryWithIcon

class StorageLister(applicationContext: Context) : AndroidStorageLister (applicationContext) {

    val defaultStorage: AndroidStorageDirectory get() = storageDirectories.first()

    override fun createStorageDirectory(
        type: AndroidStorageType,
        name: String,
        path: String
    ): AndroidStorageDirectory {
        return StorageDirectoryWithIcon(
            name = name,
            path = path,
            type = type,
            icon = iconForType(type)
        )
    }

    private fun iconForType(type: AndroidStorageType): Int {
        return when(type) {
            AndroidStorageType.INTERNAL -> R.drawable.ic_storage_type_internal
            AndroidStorageType.USB -> R.drawable.ic_storage_type_usb
            AndroidStorageType.SD_CARD -> R.drawable.ic_storage_type_sd_card
        }
    }
}
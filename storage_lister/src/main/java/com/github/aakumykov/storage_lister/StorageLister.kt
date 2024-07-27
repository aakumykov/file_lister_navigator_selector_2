package com.github.aakumykov.storage_lister

import android.content.Context
import androidx.annotation.DrawableRes
import com.github.aakumykov.android_storage_lister.AndroidStorageDirectory
import com.github.aakumykov.android_storage_lister.AndroidStorageLister
import com.github.aakumykov.android_storage_lister.AndroidStorageType

class StorageLister(applicationContext: Context): AndroidStorageLister(applicationContext) {

    override fun createStorageDirectory(
        type: AndroidStorageType,
        name: String,
        path: String
    ): StorageDirectory {
        return StorageDirectory(
            type = type,
            name = name,
            path = path,
            icon = type2icon(type)
        )
    }

    @DrawableRes
    private fun type2icon(type: AndroidStorageType): Int {
        return when(type) {
            AndroidStorageType.INTERNAL -> R.drawable.ic_storage_type_internal
            AndroidStorageType.SD_CARD -> R.drawable.ic_storage_type_sd_card
            AndroidStorageType.USB -> R.drawable.ic_storage_type_usb
        }
    }

}
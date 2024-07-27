package com.github.aakumykov.storage_lister

import androidx.annotation.DrawableRes
import com.github.aakumykov.android_storage_lister.AndroidStorageDirectory
import com.github.aakumykov.android_storage_lister.AndroidStorageType

class StorageDirectory(
    override val name: String,
    override val path: String,
    override val type: AndroidStorageType,
    @DrawableRes val icon: Int
): AndroidStorageDirectory
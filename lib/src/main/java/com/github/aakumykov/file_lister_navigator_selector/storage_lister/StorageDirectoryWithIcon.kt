package com.github.aakumykov.file_lister_navigator_selector.storage_lister

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.github.aakumykov.android_storage_lister.AndroidStorageDirectory
import com.github.aakumykov.android_storage_lister.AndroidStorageType
import kotlinx.parcelize.Parcelize

@Parcelize
data class StorageDirectoryWithIcon(
    override val type: AndroidStorageType,
    override val path: String,
    override val name: String,
    @DrawableRes val icon: Int,
): AndroidStorageDirectory, Parcelable
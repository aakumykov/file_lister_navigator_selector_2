package com.github.aakumykov.storage_lister

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.github.aakumykov.android_storage_lister.AndroidStorageDirectory
import com.github.aakumykov.android_storage_lister.AndroidStorageType
import kotlinx.parcelize.Parcelize


@Parcelize
sealed class StorageDirectory(
    override val name: String,
    override val path: String,
    override val type: AndroidStorageType,
    @DrawableRes val icon: Int
): AndroidStorageDirectory, Parcelable


class InternalStorageDirectory(
    override val name: String,
    override val path: String
): StorageDirectory(
    name = name,
    path = path,
    type = AndroidStorageType.INTERNAL,
    icon = R.drawable.ic_storage_type_internal
)


class SDCardDirectory(
    override val name: String,
    override val path: String
): StorageDirectory(
    name = name,
    path = path,
    type = AndroidStorageType.SD_CARD,
    icon = R.drawable.ic_storage_type_sd_card
)


class USBDriveDirectory(
    override val name: String,
    override val path: String
): StorageDirectory(
    name = name,
    path = path,
    type = AndroidStorageType.USB,
    icon = R.drawable.ic_storage_type_usb
)


class DummyStorageDirectory: StorageDirectory(
    name = "",
    path = "",
    type = AndroidStorageType.SD_CARD,
    icon = R.drawable.ic_storage_dummy
)
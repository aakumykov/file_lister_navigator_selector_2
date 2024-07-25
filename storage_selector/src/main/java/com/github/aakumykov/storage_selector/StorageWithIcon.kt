package com.github.aakumykov.storage_selector

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.github.aakumykov.android_storage_lister.AndroidStorageDirectory
import com.github.aakumykov.android_storage_lister.AndroidStorageType
import kotlinx.parcelize.Parcelize

@Parcelize
class StorageWithIcon(
    val name: String,
    val path: String,
    val type: AndroidStorageType,
    @DrawableRes val icon: Int
)
    : Parcelable
{

    val description: String get() = StorageWithIcon::class.java.simpleName + " { [$type] name: $name ($path) }"

    override fun equals(other: Any?): Boolean {
        return when {
            (null == other) -> false
            (other !is StorageWithIcon) -> false
            else -> propertiesAreTheSame(other)
        }
    }

    private fun propertiesAreTheSame(otherStorage: StorageWithIcon): Boolean {
        return otherStorage.name == name &&
                otherStorage.path == path
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + icon
        return result
    }

    companion object {
        fun create(androidStorageDirectory: AndroidStorageDirectory): StorageWithIcon {
            return StorageWithIcon(
                name = androidStorageDirectory.name,
                path = androidStorageDirectory.path,
                type = androidStorageDirectory.type,
                icon = iconFor(androidStorageDirectory.type)
            )
        }

        @DrawableRes
        fun iconFor(storageType: AndroidStorageType): Int {
            return when(storageType) {
                AndroidStorageType.INTERNAL -> R.drawable.ic_storage_type_internal
                AndroidStorageType.SD_CARD -> R.drawable.ic_storage_type_sd_card
                AndroidStorageType.USB -> R.drawable.ic_storage_type_usb
            }
        }
    }
}

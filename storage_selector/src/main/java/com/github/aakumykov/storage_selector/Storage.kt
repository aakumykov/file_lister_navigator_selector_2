package com.github.aakumykov.storage_selector

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
@Deprecated("Переименовать в SelectableStorage")
class Storage(
    val name: String,
    val path: String,
    val type: StorageType,
    @DrawableRes val icon: Int
): Parcelable {

    val description: String get() = Storage::class.java.simpleName + " { [$type] name: $name ($path) }"

    override fun equals(other: Any?): Boolean {
        return when {
            (null == other) -> false
            (other !is Storage) -> false
            else -> propertiesAreTheSame(other)
        }
    }

    private fun propertiesAreTheSame(otherStorage: Storage): Boolean {
        return otherStorage.name == name &&
                otherStorage.path == path
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + icon
        return result
    }
}

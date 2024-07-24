package com.github.aakumykov.storage_selector

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
class Storage(
    val name: String,
    val path: String,
    @DrawableRes val icon: Int
): Parcelable {
    fun describe(): String = Storage::class.java.simpleName + " { $name, $path }"

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

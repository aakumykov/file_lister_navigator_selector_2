package com.github.aakumykov.file_lister_navigator_selector_demo.ext

import android.app.Activity
import android.preference.PreferenceManager
import androidx.core.content.edit

// String
fun Activity.storeStringInPreferences(key: String, value: String?) {
    PreferenceManager.getDefaultSharedPreferences(this).edit(commit = true) {
        putString(key, value)
    }
}

fun Activity.getStringFromPreferences(key: String): String? {
    return PreferenceManager.getDefaultSharedPreferences(this)
        .getString(key, null)
}

fun Activity.eraseStringFromPreferences(key: String) {
    PreferenceManager.getDefaultSharedPreferences(this).edit(commit = true) {
        remove(key)
    }
}
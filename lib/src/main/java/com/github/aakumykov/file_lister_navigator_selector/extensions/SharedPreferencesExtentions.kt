package com.github.aakumykov.file_lister_navigator_selector.extensions

import android.annotation.SuppressLint
import android.app.Activity
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager


@SuppressLint("ApplySharedPref")
fun Activity.storeStringInPreferences(key: String, value: String) {
    PreferenceManager.getDefaultSharedPreferences(this).edit(commit = true) {
        putString(key, value)
    }
}

@SuppressLint("ApplySharedPref")
fun Activity.eraseStringFromPreferences(key: String) {
    PreferenceManager.getDefaultSharedPreferences(this).edit(commit = true) {
        putString(key, null)
    }
}

fun Activity.getStringFromPreferences(key: String): String? {
    return PreferenceManager.getDefaultSharedPreferences(this)
        .getString(key, null)
}

@SuppressLint("ApplySharedPref")
fun Fragment.storeStringInPreferences(key: String, value: String?) {
    PreferenceManager.getDefaultSharedPreferences(requireContext()).edit(commit = true) {
        putString(key, value)
    }
}

@SuppressLint("ApplySharedPref")
fun Fragment.eraseStringFromPreferences(key: String) {
    PreferenceManager.getDefaultSharedPreferences(requireContext()).edit(commit = true) {
        putString(key, null)
    }
}

fun Fragment.getStringFromPreferences(key: String): String? {
    return PreferenceManager.getDefaultSharedPreferences(requireContext())
        .getString(key, null)
}
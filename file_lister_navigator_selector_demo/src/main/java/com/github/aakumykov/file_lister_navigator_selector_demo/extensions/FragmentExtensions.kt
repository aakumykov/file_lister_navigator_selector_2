package com.github.aakumykov.file_lister_navigator_selector_demo.extensions

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

fun Fragment.showToast(text: String) {
    Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(@StringRes strRes: Int) {
    showToast(getString(strRes))
}

fun Fragment.storeString(key: String, value: String?): Unit {
    androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext())
        .edit()
        .putString(key, value)
        .apply()
}

fun Fragment.restoreString(key: String): String? {
    return androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext())
        .getString(key, null)
}
package com.github.aakumykov.file_lister_navigator_selector_demo.extensions

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

fun Fragment.showToast(text: String) {
    lifecycleScope.launch {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }
}

fun Fragment.showToast(@StringRes strRes: Int) {
    lifecycleScope.launch {
        showToast(getString(strRes))
    }
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
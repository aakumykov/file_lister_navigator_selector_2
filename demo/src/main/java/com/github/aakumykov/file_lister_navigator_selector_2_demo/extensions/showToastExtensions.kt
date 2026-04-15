package com.github.aakumykov.file_lister_navigator_selector_2_demo.extensions

import android.app.Activity
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment


fun Activity.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Activity.showToast(@StringRes strRes: Int) {
    showToast(getString(strRes))
}


fun Fragment.showToast(text: String) {
    Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(@StringRes strRes: Int) {
    showToast(getString(strRes))
}
package com.github.aakumykov.file_lister_navigator_selector.extensions

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener

fun Fragment.listenForFragmentResult(requestKey: String, listener: FragmentResultListener) {
    childFragmentManager.setFragmentResultListener(requestKey, viewLifecycleOwner, listener)
}

fun Fragment.showToast(@StringRes stringRes: Int) {
    Toast.makeText(requireContext(), stringRes, Toast.LENGTH_SHORT).show()
}
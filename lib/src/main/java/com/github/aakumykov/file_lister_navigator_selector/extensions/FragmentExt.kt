package com.github.aakumykov.file_lister_navigator_selector.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener

fun Fragment.listenForFragmentResult(requestKey: String, listener: FragmentResultListener) {
    childFragmentManager.setFragmentResultListener(requestKey, viewLifecycleOwner, listener)
}

fun Fragment.qwerty() {

}
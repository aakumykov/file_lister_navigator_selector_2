package com.github.aakumykov.file_lister_navigator_selector.extensions

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentResultListener

fun FragmentActivity.listenForFragmentResult(requestKey: String, listener: FragmentResultListener) {
    supportFragmentManager.setFragmentResultListener(requestKey, this, listener)
}
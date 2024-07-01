package com.github.aakumykov.file_lister_navigator_selector.extensions

import android.view.View

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.showIf(condition: View.() -> Boolean) {
    visibility = if (condition()) View.VISIBLE else View.GONE
}

fun View.hideIf(condition: View.() -> Boolean) {
    visibility = if (condition()) View.GONE else View.VISIBLE
}
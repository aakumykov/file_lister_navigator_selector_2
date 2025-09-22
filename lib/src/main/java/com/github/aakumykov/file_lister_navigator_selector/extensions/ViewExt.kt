package com.github.aakumykov.file_lister_navigator_selector.extensions

import android.view.View

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.showIf(condition: View.() -> Boolean) {
    visibility = if (condition()) View.VISIBLE else View.INVISIBLE
}

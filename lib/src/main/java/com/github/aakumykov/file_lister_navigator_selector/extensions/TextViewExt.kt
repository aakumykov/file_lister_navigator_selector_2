package com.github.aakumykov.file_lister_navigator_selector.extensions

import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

fun TextView.setColorResource(@ColorRes colorRes: Int) {
    setTextColor(ContextCompat.getColor(context, colorRes),)
}
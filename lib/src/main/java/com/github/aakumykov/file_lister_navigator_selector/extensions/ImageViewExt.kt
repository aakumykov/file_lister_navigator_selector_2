package com.github.aakumykov.file_lister_navigator_selector.extensions

import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

fun ImageView.colorize(@ColorRes colorRes: Int) {
    setColorFilter(
        ContextCompat.getColor(context, colorRes),
        android.graphics.PorterDuff.Mode.SRC_IN
    )
}
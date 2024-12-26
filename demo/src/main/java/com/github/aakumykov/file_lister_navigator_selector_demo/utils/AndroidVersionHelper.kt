package com.github.aakumykov.file_lister_navigator_selector_demo.utils

import android.os.Build

class AndroidVersionHelper {
    companion object {
        fun is_android_R_or_later(): Boolean
            = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    }
}
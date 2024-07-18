package com.github.aakumykov.file_lister_navigator_selector.entities

import androidx.annotation.DrawableRes

data class Storage(
    @DrawableRes val iconId: Int,
    val label: String,
    val absolutePath: String,
)
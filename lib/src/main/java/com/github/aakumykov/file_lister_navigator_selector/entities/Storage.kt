package com.github.aakumykov.file_lister_navigator_selector.entities

import androidx.annotation.DrawableRes

data class Storage(
    val name: String,
    val path: String,
    @DrawableRes val icon: Int,
)
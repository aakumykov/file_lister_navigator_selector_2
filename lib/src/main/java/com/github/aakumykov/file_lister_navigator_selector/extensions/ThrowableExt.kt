package com.github.aakumykov.file_lister_navigator_selector.extensions

val Throwable.errorMsg: String
    get() = message ?: javaClass.name
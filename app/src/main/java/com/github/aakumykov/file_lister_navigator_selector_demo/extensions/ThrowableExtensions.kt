package com.github.aakumykov.file_lister_navigator_selector_demo.extensions

val Throwable.errorMsg: String get() = message ?: javaClass.name
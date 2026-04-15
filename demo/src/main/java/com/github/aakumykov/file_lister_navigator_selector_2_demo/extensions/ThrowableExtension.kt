package com.github.aakumykov.file_lister_navigator_selector_2_demo.extensions

val Throwable.errorMsg: String get() = message ?: javaClass.name

val Throwable.errorMsgExtended: String get() = message?.let { "$it (${javaClass.name})" } ?: javaClass.name
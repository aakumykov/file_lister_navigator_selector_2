package com.github.aakumykov.file_lister_navigator_selector.dir_creator

fun String.stripExtraSlashes(): String
    = this.replace(Regex("/+"),"/")
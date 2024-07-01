package com.github.aakumykov.file_lister_navigator_selector.dir_creator

import java.io.IOException

interface DirCreator {

    @Throws(IOException::class, UnsuccessfulOperationException::class)
    fun makeDir(absoluteDirPath: String)

    class UnsuccessfulOperationException(message: String) : Exception(message)
}
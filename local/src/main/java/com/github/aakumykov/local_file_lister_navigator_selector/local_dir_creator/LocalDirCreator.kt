package com.github.aakumykov.local_file_lister_navigator_selector.local_dir_creator

import com.github.aakumykov.file_lister_navigator_selector.dir_creator.DirCreator
import java.io.File
import java.io.IOException

class LocalDirCreator : DirCreator {

    @Throws(IOException::class, DirCreator.UnsuccessfulOperationException::class)
    override fun makeDir(absoluteDirPath: String) {

        val dir = File(absoluteDirPath)

        if (dir.exists())
            throw DirCreator.UnsuccessfulOperationException("Dir '$absoluteDirPath' already exists.")

        if (!dir.mkdir())
            throw DirCreator.UnsuccessfulOperationException("Dir '$absoluteDirPath' is not created.")
    }
}
package com.github.aakumykov.file_lister_navigator_selector.fs_item

import com.github.aakumykov.file_lister_navigator_selector.fs_item.utils.parentPathFor
import java.io.File

open class SimpleFSItem(
    override val name: String,
    override val absolutePath: String,
    override val parentPath: String,
    override val isDir: Boolean,
    override val mTime: Long,
    override val size: Long
) : FSItem {

    constructor(file: File) : this(
        name = file.name,
        absolutePath = file.absolutePath,
        parentPath = parentPathFor(file.absolutePath),
        isDir = file.isDirectory,
        mTime = file.lastModified(),
        size = file.length()
    )

    override fun toString(): String = TAG + " { ${nameAndPath()} }"

    protected fun nameAndPath(): String = "name: $name (absolutePath: $absolutePath)"

    companion object {
        val TAG: String = SimpleFSItem::class.java.simpleName
    }
}
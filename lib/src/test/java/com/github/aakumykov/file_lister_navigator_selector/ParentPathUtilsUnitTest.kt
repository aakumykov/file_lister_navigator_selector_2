package com.github.aakumykov.file_lister_navigator_selector

import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.file_lister_navigator_selector.fs_item.utils.parentPathFor
import org.junit.Assert
import org.junit.Test

class ParentPathUtilsUnitTest {

    private val emptyPath = FSItem.NO_PARENT_PATH
    private val doubleDotPath = FSItem.PARENT_DIR_PATH

    private val dirName = "qwerty"
    private val emptyDirName = " "

    private val relativeParentPath = "a/b"
    private val absoluteParentPath = "/$relativeParentPath"
    private val relativeDeepPath = "${relativeParentPath}/$dirName"
    private val absoluteDeepPath = "${absoluteParentPath}/$dirName"


    @Test
    fun when_empty_parent_then_returns_itself() {
        Assert.assertEquals(
            parentPathFor(emptyPath),
            emptyPath
        )
    }

    @Test
    fun when_double_dot_parent_then_returns_itself() {
        Assert.assertEquals(
            parentPathFor(doubleDotPath),
            doubleDotPath
        )
    }

    @Test
    fun when_simple_dir_name_then_returns_itself() {
        Assert.assertEquals(
            parentPathFor(dirName),
            dirName
        )
    }

    @Test
    fun when_empty_dir_name_then_returns_itself() {
        Assert.assertEquals(
            parentPathFor(emptyDirName),
            emptyDirName
        )
    }

    @Test
    fun when_relative_deep_path_then_returns_relative_parent() {
        Assert.assertEquals(
            parentPathFor(relativeDeepPath),
            relativeParentPath
        )
    }

    @Test
    fun when_absolute_deep_path_then_returns_absolute_parent() {
        Assert.assertEquals(
            parentPathFor(absoluteDeepPath),
            absoluteParentPath
        )
    }
}
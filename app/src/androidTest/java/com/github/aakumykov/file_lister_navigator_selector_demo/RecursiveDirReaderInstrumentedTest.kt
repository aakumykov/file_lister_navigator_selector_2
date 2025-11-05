package com.github.aakumykov.file_lister_navigator_selector_demo

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.aakumykov.file_lister_navigator_selector.file_lister.FileLister
import com.github.aakumykov.file_lister_navigator_selector.recursive_dir_reader.RecursiveDirReader
import com.github.aakumykov.local_file_lister_navigator_selector.local_file_lister.LocalFileLister
import com.kaspersky.components.kautomator.common.Environment
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.UUID


@RunWith(AndroidJUnit4::class)
class RecursiveDirReaderInstrumentedTest : StorageAccessTestCase() {

    private val localRecursiveDirReader: RecursiveDirReader
        get() = RecursiveDirReader(LocalFileLister())

    val cacheDir: File
        get() = InstrumentationRegistry.getInstrumentation().targetContext.cacheDir

    val externalStorageDir: File
        get() = android.os.Environment.getExternalStorageDirectory()


    @Test
    fun reading_system_root_dir_throws_exception() {
        Assert.assertThrows(FileLister.CannotReadException::class.java) {
            localRecursiveDirReader.listDirRecursively("/")
        }
    }

    @Test
    fun reading_dir_with_empty_path() {
        Assert.assertThrows(FileNotFoundException::class.java) {
            localRecursiveDirReader.listDirRecursively("")
        }
    }

    @Test
    fun can_read_cache_dir() {
        Assert.assertTrue(
            File(cacheDir, UUID.randomUUID().toString()).createNewFile()
        )
        Assert.assertTrue(
            localRecursiveDirReader.listDirRecursively(cacheDir.absolutePath).isNotEmpty()
        )
    }

    @Test
    fun reading_external_storage_dir() {
        Assert.assertTrue(
            localRecursiveDirReader.listDirRecursively(externalStorageDir.absolutePath).isNotEmpty()
        )
    }
}
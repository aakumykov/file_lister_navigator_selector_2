package com.github.aakumykov.file_lister_navigator_selector_demo

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.aakumykov.file_lister_navigator_selector.file_lister.FileLister
import com.github.aakumykov.file_lister_navigator_selector.recursive_dir_reader.RecursiveDirReader
import com.github.aakumykov.local_file_lister_navigator_selector.local_file_lister.LocalFileLister
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileNotFoundException
import java.util.UUID
import kotlin.random.Random


@RunWith(AndroidJUnit4::class)
class LocalRecursiveDirReaderInstrumentedTest : StorageAccessTestCase() {

    /**
     * Проверка аргумента:
     * - чтение каталога с именем нулевой длины ("") [zero_length_name_dir_reading]
     * - каталога с пустым (empty) именем [empty_name_dir_reading]
     * - чтение каталога с кривым именем [struggly_name_dir_reading]
     *
     * "Плохой" читаемый каталог:
     * - чтение несуществующего каталога [unexistent_dir_reading]
     * - чтение каталога, недоступного на чтение [unreadable_dir_reading]
     * - попытка чтения файла как каталога [reading_file_as_dir]
     *
     * Особый вариант:
     * - чтение корневого каталога [system_root_dir_reading]
     *
     * "Хороший" читаемый каталог:
     * - чтение пустого каталога [empty_dir_reading]
     * - чтение каталога с плоским набором файлов [flat_contents_dir_reading]
     * - чтение каталога с деревом файлов [tree_contents_dir_reading]
     */

    companion object {
        const val ZERO_LENGTH_NAME = ""
        const val EMPTY_NAME = " "
        const val SYSTEM_ROOT_DIR = "/"
        const val UNREADABLE_DIR = SYSTEM_ROOT_DIR
    }

    private val STRUGGLY_NAME: String
        get() = Random.nextBytes(10).joinToString()

    private val randomName: String
        get() = UUID.randomUUID().toString()

    private val recursiveDirReader: RecursiveDirReader
        get() = RecursiveDirReader(LocalFileLister())

    private val cacheDir: File
        get() = InstrumentationRegistry.getInstrumentation().targetContext.cacheDir

    private val externalStorageDir: File
        get() = android.os.Environment.getExternalStorageDirectory()


    @Test
    fun zero_length_name_dir_reading() {
        Assert.assertThrows(FileNotFoundException::class.java) {
            recursiveDirReader.listDirRecursively(ZERO_LENGTH_NAME)
        }
    }


    @Test
    fun empty_name_dir_reading() {
        Assert.assertThrows(FileNotFoundException::class.java) {
            recursiveDirReader.listDirRecursively(EMPTY_NAME)
        }
    }


    @Test
    fun struggly_name_dir_reading() {
        Assert.assertThrows(FileNotFoundException::class.java) {
            recursiveDirReader.listDirRecursively(STRUGGLY_NAME)
        }
    }


    @Test
    fun system_root_dir_reading() {
        Assert.assertThrows(FileLister.CannotReadException::class.java) {
            recursiveDirReader.listDirRecursively(SYSTEM_ROOT_DIR)
        }
    }


    @Test
    fun unexistent_dir_reading() {
        Assert.assertThrows(FileNotFoundException::class.java) {
            recursiveDirReader.listDirRecursively(randomName)
        }
    }


    @Test
    fun unreadable_dir_reading() {
        Assert.assertThrows(FileLister.CannotReadException::class.java) {
            recursiveDirReader.listDirRecursively(UNREADABLE_DIR)
        }
    }


    @Test
    fun reading_file_as_dir() {
        Assert.assertThrows(FileLister.NotADirException::class.java) {
            val file = File(cacheDir, randomName)
            Assert.assertTrue(file.createNewFile())
            recursiveDirReader.listDirRecursively(file.absolutePath)
        }
    }

    @Test
    fun empty_dir_reading() {
        prepareEmptyDir()
        Assert.assertTrue(
            recursiveDirReader.listDirRecursively(cacheDir.absolutePath).isNotEmpty()
        )
    }


    @Test
    fun flat_contents_dir_reading() {
        repeat(10) { i ->
            val parentDir = prepareEmptyDir()
            repeat(i) { prepareEmptyDir(parentDir) }
            val list = recursiveDirReader.listDirRecursively(parentDir.absolutePath)
            Assert.assertEquals(i, list.size)
        }
    }


    @Test
    fun tree_contents_dir_reading() {
        Assert.assertTrue(
            recursiveDirReader.listDirRecursively(externalStorageDir.absolutePath).isNotEmpty()
        )
    }


    private fun prepareEmptyDir(parentDir: File = cacheDir): File {
        val dir = File(parentDir, randomName)
        Assert.assertTrue(dir.mkdir())
        Assert.assertTrue(dir.list().isEmpty())
        return dir
    }
}
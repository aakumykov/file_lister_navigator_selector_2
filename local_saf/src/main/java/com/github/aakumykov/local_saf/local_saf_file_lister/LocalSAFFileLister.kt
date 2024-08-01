package com.github.aakumykov.local_saf.local_saf_file_lister

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.github.aakumykov.file_lister_navigator_selector.file_lister.FileLister
import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode
import com.github.aakumykov.file_lister_navigator_selector.fs_item.DirItem
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.file_lister_navigator_selector.fs_item.SimpleFSItem
import com.github.aakumykov.local_saf.LocalCloudReaderForStorageAccessFramework
import java.io.File

class LocalSAFFileLister(
    private val applicationContext: Context,
    private val localSAFCloudReader: LocalCloudReaderForStorageAccessFramework
)
    : FileLister<SimpleSortingMode>
{

    override fun listDir(
        path: String,
        sortingMode: SimpleSortingMode,
        reverseOrder: Boolean,
        foldersFirst: Boolean,
        dirMode: Boolean
    ): List<FSItem> {
        return DocumentFile.fromTreeUri(applicationContext, Uri.parse(path))
            ?.listFiles()
            ?.map { documentFile ->
                when(documentFile.isDirectory) {
                    true -> DirItem.fromPath(documentFile.uri.toString())
                    false -> SimpleFSItem(File(documentFile.uri.toString()))
                }
            } ?: emptyList()
    }


    override suspend fun fileExists(path: String): Result<Boolean>
        = localSAFCloudReader.fileExists(path)

}

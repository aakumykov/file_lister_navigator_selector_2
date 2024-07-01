package com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_file_lister

import android.util.Log
import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.file_lister_navigator_selector.fs_item.SimpleFSItem
import com.github.aakumykov.file_lister_navigator_selector.fs_item.utils.parentPathFor
import com.github.aakumykov.yandex_disk_client.YandexDiskClient
import com.github.aakumykov.yandex_disk_client.YandexDiskSortingMode
import com.yandex.disk.rest.json.Resource

class FileListerYandexDiskClient(authToken: String)
    : YandexDiskClient<FSItem, SimpleSortingMode>(authToken)
{
    override fun appToDiskSortingMode(appSortingMode: SimpleSortingMode, reverseOrder: Boolean): YandexDiskSortingMode {

        Log.d(TAG, "appToDiskSortingMode(), mode: $appSortingMode, reverseOrder: $reverseOrder")

        return when(appSortingMode) {
            SimpleSortingMode.NAME -> if (reverseOrder) YandexDiskSortingMode.NAME_REVERSE else YandexDiskSortingMode.NAME_DIRECT
            SimpleSortingMode.C_TIME -> if (reverseOrder) YandexDiskSortingMode.C_TIME_FROM_OLD_TO_NEW else YandexDiskSortingMode.C_TIME_FROM_NEW_TO_OLD
            SimpleSortingMode.M_TIME -> if (reverseOrder) YandexDiskSortingMode.M_TIME_FROM_OLD_TO_NEW else YandexDiskSortingMode.M_TIME_FROM_NEW_TO_OLD
            SimpleSortingMode.SIZE -> if (reverseOrder) YandexDiskSortingMode.SIZE_FROM_SMALL_TO_BIG else YandexDiskSortingMode.SIZE_FROM_BIG_TO_SMALL
        }
    }

    override fun cloudItemToLocalItem(resource: Resource): FSItem {

        val path = resource.path.path
        val parentPath = parentPathFor(path)

        return SimpleFSItem(
            name = resource.name,
            absolutePath = path,
            parentPath = parentPath,
            isDir = resource.isDir,
            mTime = resource.modified.time,
            size = resource.size
        )
    }

    override fun cloudFileToString(resource: Resource): String {
        return "Yandex Disk item '${resource.name}'"
    }

    companion object {
        val TAG: String = FileListerYandexDiskClient::class.java.simpleName
    }
}

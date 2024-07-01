package com.github.aakumykov.file_lister_navigator_selector_demo.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.aakumykov.file_lister_navigator_selector.file_explorer.FileExplorer
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem

abstract class BasicViewModel<SortingModeType> : ViewModel(), FileExplorer.ListCache, FileExplorer.PathCache {

    protected val _currentList: MutableLiveData<List<FSItem>> = MutableLiveData()
    val currentList: LiveData<List<FSItem>> = _currentList

    protected val _currentPath: MutableLiveData<String> = MutableLiveData()
    val currentPath: LiveData<String> = _currentPath


    abstract fun getFileExplorer(): FileExplorer<SortingModeType>
}
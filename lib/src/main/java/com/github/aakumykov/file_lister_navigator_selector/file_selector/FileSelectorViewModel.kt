package com.github.aakumykov.file_lister_navigator_selector.file_selector

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.file_lister_navigator_selector.StorageLister
import com.github.aakumykov.file_lister_navigator_selector.entities.Storage
import com.github.aakumykov.file_lister_navigator_selector.file_explorer.FileExplorer
import com.github.aakumykov.file_lister_navigator_selector.fs_item.DirItem
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.file_lister_navigator_selector.fs_item.ParentDirItem
import com.github.aakumykov.file_lister_navigator_selector.utils.StorageDetector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FileSelectorViewModel<SortingModeType> (
    val fileExplorer: FileExplorer<SortingModeType>,
    private val storageLister: StorageLister,
    private var isMultipleSelectionMode: Boolean,
)
    : ViewModel()
{
    private val _storageList: MutableLiveData<List<Storage>> = MutableLiveData()
    private val _selectedStorage: MutableLiveData<Storage> = MutableLiveData()

    private val _currentPath: MutableLiveData<String> = MutableLiveData()
    private val _currentList: MutableLiveData<List<FSItem>> = MutableLiveData(emptyList())
    private val _selectedList: MutableLiveData<List<FSItem>> = MutableLiveData(emptyList())
    private val _currentError: MutableLiveData<Throwable> = MutableLiveData()
    private val _isBusy: MutableLiveData<Boolean> = MutableLiveData()
    private val _isDirMode: MutableLiveData<Boolean> = MutableLiveData()

    private val selectedItems: MutableList<FSItem> = mutableListOf()

    val storageList: LiveData<List<Storage>> = _storageList
    val selectedStorage: LiveData<Storage> = _selectedStorage

    val path: LiveData<String> = _currentPath
    val list: LiveData<List<FSItem>> = _currentList
    val selectedList: LiveData<List<FSItem>> = _selectedList
    val errorMsg: LiveData<Throwable> = _currentError
    val isBusy: LiveData<Boolean> = _isBusy
    val isDirMode: LiveData<Boolean> = _isDirMode

    val currentSortingMode get() = fileExplorer.getSortingMode()
    val isReverseOrder: Boolean get() = fileExplorer.getReverseOrder()
    val isFoldersFirst: Boolean get() = fileExplorer.getFoldersFirst()

    private val isSingleSelectionMode get() = !isMultipleSelectionMode

    fun startWork() {
        detectStorages()
        listCurrentPath()
    }

    private fun detectStorages() {
        storageLister.listStorages().also { list ->
            if (list.size > 0) {
                _storageList.value = list
                _selectedStorage.value = list.first()
            }
        }
    }

    fun reopenCurrentDir() {
        listCurrentPath()
    }

    fun onItemClick(position: Int) {
        getItemAtPosition(position).also { fsItem ->
            fsItem?.also {
                if (fsItem.isDir) {
                    fileExplorer.changeDir(fsItem as DirItem)
                    listCurrentPath()
                }
            }
        }
    }

    fun onItemLongClick(position: Int) {
        getItemAtPosition(position)?.also { longClickedItem ->

            // Не позволяю "выбрать" ссылку на родительский каталог.
            if (longClickedItem is ParentDirItem)
                return

            if (selectedItems.contains(longClickedItem))
                selectedItems.remove(longClickedItem)
            else {
                if (isSingleSelectionMode)
                    selectedItems.clear()
                selectedItems.add(longClickedItem)
            }

            _selectedList.value = selectedItems
        }
    }

    fun onBackClicked() {
        fileExplorer.changeDir(ParentDirItem())
        listCurrentPath()
    }

    private fun getItemAtPosition(position: Int): FSItem? {
        return _currentList.value?.get(position)
    }

    private fun listCurrentPath() {

        _currentPath.value = fileExplorer.getCurrentPath()
        _selectedList.value = emptyList()

        viewModelScope.launch {

            _isBusy.value = true

            try {
                withContext(Dispatchers.IO) {
                    fileExplorer.listCurrentPath()
                }.also {
                    _currentList.value = it
                }
            } catch (e: Exception) {
                _currentError.value = e
            } finally {
                _isBusy.value = false
            }
        }
    }

    fun changeSortingMode(sortingMode: SortingModeType) {
        fileExplorer.changeSortingMode(sortingMode)
        listCurrentPath()
    }

    fun changeReverseOrder(isReverseOrder: Boolean) {
        fileExplorer.setReverseOrder(isReverseOrder)
        listCurrentPath()
    }

    fun changeFoldersFist(foldersFirst: Boolean) {
        fileExplorer.setFoldersFirst(foldersFirst)
        listCurrentPath()
    }


    fun changeSelectedStorage(storage: Storage) {
        _selectedStorage.value = storage
        fileExplorer.changeDir(DirItem.fromPath(storage.absolutePath))
        listCurrentPath()
    }


    class Factory<SortingModeType>(
        private val fileExplorer: FileExplorer<SortingModeType>,
        private val storageLister: StorageLister,
        private val isMultipleSelectionMode: Boolean
    )
        : ViewModelProvider.Factory
    {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FileSelectorViewModel(
                fileExplorer,
                storageLister,
                isMultipleSelectionMode,
            ) as T
        }
    }
}
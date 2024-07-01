package com.github.aakumykov.file_lister_navigator_selector.dir_creator_dialog

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.github.aakumykov.file_lister_navigator_selector.dir_creator.DirCreator
import kotlin.concurrent.thread

class DirCreatorViewModel(private val dirCreator: DirCreator) : ViewModel() {

    fun createDir(absoluteDirPath: String, handler: Handler) {
        thread {
            try {
                dirCreator.makeDir(absoluteDirPath)
                handler.post { _isOperationSuccess.postValue(true) }
            }
            catch (e: Exception) {
                handler.post {
                    _isOperationSuccess.postValue(false)
                    _error.value = e
                }
            }
        }
    }

    private val _isOperationSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val isOperationSuccess: LiveData<Boolean> = _isOperationSuccess

    private val _error: MutableLiveData<Exception> = MutableLiveData()
    val errorMsg: LiveData<Exception> = _error


    companion object {
        fun factory(dirCreator: DirCreator): ViewModelProvider.Factory = object: ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return DirCreatorViewModel(dirCreator) as T
            }
        }
    }
}

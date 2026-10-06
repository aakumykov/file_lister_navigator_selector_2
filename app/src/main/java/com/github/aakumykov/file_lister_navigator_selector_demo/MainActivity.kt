package com.github.aakumykov.file_lister_navigator_selector_demo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.aakumykov.cloud_authenticator.CloudAuthenticator
import com.github.aakumykov.file_lister_navigator_selector.extensions.errorMsg
import com.github.aakumykov.file_lister_navigator_selector.file_selector.FileSelector
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.file_lister_navigator_selector_demo.databinding.ActivityMainBinding
import com.github.aakumykov.file_lister_navigator_selector_demo.ext.getStringFromPreferences
import com.github.aakumykov.file_lister_navigator_selector_demo.ext.storeStringInPreferences
import com.github.aakumykov.local_file_lister_navigator_selector.local_file_selector.LocalFileSelector
import com.github.aakumykov.storage_access_helper.StorageAccessHelper
import com.github.aakumykov.yandex_authenticator.YandexAuthenticator
import com.github.aakumykov.yandex_disk_cloud_writer.YandexDiskCloudWriter
import com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_dir_creator.YandexDiskDirCreator
import com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_file_lister.YandexDiskFileLister
import com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_file_selector.YandexDiskFileSelector
import com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_fs_navigator.YandexDiskFileExplorer

class MainActivity : AppCompatActivity(), CloudAuthenticator.Callbacks, FileSelector.Callbacks {

    private lateinit var binding: ActivityMainBinding
    private val storageAccessHelper: StorageAccessHelper by lazy { StorageAccessHelper.create(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        storageAccessHelper.prepareForReadAccess()

        binding.selectFileLocallyButton.setOnClickListener { onSelectFileLocalClicked() }
        binding.authButton.setOnClickListener { onAuthButtonClicked() }
        binding.selectFileButton.setOnClickListener { onSelectButtonClocked() }

        yandexAuthenticator.prepare(this, CloudAuthenticator.LoginType.NATIVE, true)

        restoreViewState()
    }

    override fun onCloudAuthSuccess(authToken: String) {
        super.onCloudAuthSuccess(authToken)
        this.authToken = authToken
        restoreViewState()
    }

    private fun showViewStateAuthYes() {
        binding.authButton.text = "Забыть авторизацию"
        showInfo(authToken!!)
    }

    private fun showViewStateAuthNo() {
        binding.authButton.text = "Авторизоваться"
        hideInfo()
    }

    override fun onCloudAuthFailed(throwable: Throwable) {
        super.onCloudAuthFailed(throwable)
        showError(throwable)
        showViewStateAuthNo()
    }

    override fun onFileSelected(key: String, list: List<FSItem>) {
        showInfo("key: $key,\nselected: $list")
    }

    private fun onSelectFileLocalClicked() {
        storageAccessHelper.requestReadAccess {
            LocalFileSelector.create()
                .startSelecting(KEY_LOCAL_SELECTION, this, this)
        }
    }

    private fun onAuthButtonClicked() {
        if (null != authToken) {
            authToken = null
            hideInfo()
        } else {
            yandexAuthenticator.startAuth(this)
        }
    }

    private val yandexDiskCloudWriter get() = YandexDiskCloudWriter(authToken!!)
    private val yandexDiskFileExplorer: YandexDiskFileExplorer get() {
        return YandexDiskFileExplorer(
            yandexDiskFileLister = YandexDiskFileLister(authToken!!),
            yandexDiskDirCreator = YandexDiskDirCreator(yandexDiskCloudWriter),
            initialPath = "/",
            isDirMode = false,
        )
    }

    private fun onSelectButtonClocked() {
        authToken?.also {
            YandexDiskFileSelector
                .create(authToken = authToken!!)
                .startSelecting(KEY_CLOUD_SELECTION, this, this)
        } ?: run {
            showError("Нет авторизации")
        }
    }

    private fun showInfo(text: String) {
        binding.infoView.text = text
    }
    private fun hideInfo() {
        binding.infoView.text = ""
    }

    private fun showError(t: Throwable) {
        showError(t.errorMsg)
        t.printStackTrace()
    }
    private fun showError(text: String) {
        binding.infoView.text = text
    }
    private fun hideError() {
        binding.infoView.text = ""
    }

    private fun resetView() {
        hideError()
        hideInfo()
    }

    private fun restoreViewState() {
        if (null != authToken) {
            showViewStateAuthYes()
        } else {
            showViewStateAuthNo()
        }
    }

    private val yandexAuthenticator by lazy {
        YandexAuthenticator(this)
    }

    private var authToken: String?
        get() = getStringFromPreferences(AUTH_TOKEN)
        set(value) = storeStringInPreferences(AUTH_TOKEN, value)

    companion object {
        val TAG: String = MainActivity::class.java.simpleName
        const val AUTH_TOKEN = "AUTH_TOKEN"
        const val KEY_LOCAL_SELECTION = "LOCAL_SELECTION"
        const val KEY_CLOUD_SELECTION = "CLOUD_SELECTION"
    }
}
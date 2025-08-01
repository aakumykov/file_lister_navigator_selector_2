package com.github.aakumykov.file_lister_navigator_selector_demo.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.github.aakumykov.file_lister_navigator_selector.file_lister.FileLister
import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode
import com.github.aakumykov.file_lister_navigator_selector.file_selector.FileSelector
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.file_lister_navigator_selector.recursive_dir_reader.RecursiveDirReader
import com.github.aakumykov.file_lister_navigator_selector_demo.R
import com.github.aakumykov.file_lister_navigator_selector_demo.common.StorageType
import com.github.aakumykov.file_lister_navigator_selector_demo.databinding.FragmentDemoBinding
import com.github.aakumykov.file_lister_navigator_selector_demo.extensions.errorMsg
import com.github.aakumykov.file_lister_navigator_selector.extensions.listenForFragmentResult
import com.github.aakumykov.file_lister_navigator_selector_demo.extensions.showToast
import com.github.aakumykov.local_file_lister_navigator_selector.local_file_lister.LocalFileLister
import com.github.aakumykov.local_file_lister_navigator_selector.local_file_selector.LocalFileSelector
import com.github.aakumykov.storage_access_helper.StorageAccessHelper
import com.github.aakumykov.yandex_disk_cloud_reader.YandexDiskCloudReader
import com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_file_lister.YandexDiskFileLister
import com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_file_selector.YandexDiskFileSelector
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthResult
import com.yandex.authsdk.YandexAuthSdkContract
import com.yandex.authsdk.internal.strategy.LoginType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DemoFragment : Fragment(R.layout.fragment_demo), FragmentResultListener {

    private var _binding: FragmentDemoBinding? = null
    private val binding get()= _binding!!

    private var fileSelector: FileSelector<SimpleSortingMode>? = null

    private var yandexAuthToken: String? = null
    private lateinit var yandexAuthLauncher: ActivityResultLauncher<YandexAuthLoginOptions>

    private lateinit var storageAccessHelper: StorageAccessHelper

    private val mSelectedItemsList: MutableList<FSItem> = mutableListOf()

    private var storageType: StorageType? = null

    private var currentJob: Job? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareLayout(view)
        prepareStorageAccessHelper()
        prepareYandexAuthLauncher()
        prepareFragmentResultListener()
        prepareButtons()
        restoreYandexAuthToken(savedInstanceState)


    }

    private fun prepareFragmentResultListener() {
        listenForFragmentResult(YANDEX_DISK_SELECTION_REQUEST_KEY, this)
        listenForFragmentResult(LOCAL_SELECTION_REQUEST_KEY, this)
    }

    override fun onFragmentResult(requestKey: String, result: Bundle) {
        FileSelector.extractSelectionResult(result)?.also { list ->

            mSelectedItemsList.apply {
                clear()
                addAll(list)
            }

            binding.outputView.text = getString(
                R.string.selection_result, list.joinToString("\n") {
//                    (if (it.isDir) "dir: " else "file: ") + it.name
                    it.absolutePath
                })
        }
    }

    private fun prepareStorageAccessHelper() {
        storageAccessHelper = StorageAccessHelper.Companion.create(this).apply {
            prepareForReadAccess()
            prepareForWriteAccess()
            prepareForFullAccess()
        }
    }

    private fun prepareLayout(view: View) {
        _binding = FragmentDemoBinding.bind(view)
    }

    private fun restoreYandexAuthToken(savedInstanceState: Bundle?) {
        if (null == savedInstanceState)
            yandexAuthToken = restoreString(YANDEX_AUTH_TOKEN)
    }

    private fun prepareButtons() {
        binding.localSelectButton.setOnClickListener { onLocalSelectButtonClicked() }
        binding.yandexSelectButton.setOnClickListener { onYandexSelectButtonClicked() }
        binding.yandexSelectButton.setOnLongClickListener { return@setOnLongClickListener onYandexSelectButtonLongClicked() }
        binding.listDirRecursivelyButton.setOnClickListener { onListDirRecursivelyClicked() }
        binding.cancelRecursiveListing.setOnClickListener { onCancelRecursiveListingClicked() }
        binding.clearOutputButton.setOnClickListener { onClearOutputButtonClicked() }
    }

    private fun onListDirRecursivelyClicked() {
        if (1 == mSelectedItemsList.size && mSelectedItemsList.first().isDir) {
            listDirRecursively(mSelectedItemsList.first())
        } else {
            showToast(R.string.ERROR_single_dir_must_be_selected)
        }
    }

    private fun onCancelRecursiveListingClicked() {
        currentJob?.cancel(CancellationException("Отменено пользователем"))
            ?: run { showToast("Нет текущей задачи") }
    }

    private fun onClearOutputButtonClicked() {
        binding.outputView.text = ""
    }

    private fun listDirRecursively(dirItem: FSItem) {

        binding.outputView.text = getString(R.string.listing_dir_recursively, dirItem.absolutePath)

        showProgressBar()
        hideError()

        currentJob = lifecycleScope.launch (Dispatchers.IO) {
            try {
                RecursiveDirReader(fileLister())
                    .listDirRecursivelySuspend(
                        path = dirItem.absolutePath,
                        debug_log_each_item = true,
                        debug_each_step_delay_for_debug_ms = 0,
                    )
                    .also { displayList(dirItem, it) }
            }
            catch (e: CancellationException) {
                showError(e.errorMsg)
                showToast(e.errorMsg)
            }
            finally {
                hideProgressBar()
            }

        }
    }

    private suspend fun displayList(dirItem: FSItem, list: List<RecursiveDirReader.FileListItem>) {
        list.joinToString("\n") { it.absolutePath }
            .also {
                withContext(Dispatchers.Main) {
                    binding.progressBar.gone()
                    binding.outputView.text = getString(R.string.recursive_dir_listing_result, dirItem.name, it)
                }
            }
    }

    private fun showProgressBar() {
        lifecycleScope.launch (Dispatchers.Main) {
            binding.progressBar.visible()
        }
    }

    private fun hideProgressBar() {
        lifecycleScope.launch (Dispatchers.Main) {
            binding.progressBar.gone()
        }
    }

    private fun fileLister(): FileLister<SimpleSortingMode> {
        return when(storageType) {
            StorageType.LOCAL -> localFileLister()
            StorageType.YANDEX_DISK -> yandexDiskFileLister()
            else -> throw IllegalStateException("Неизвестное значение storageType: ${storageType}")
        }
    }

    private fun localFileLister(): LocalFileLister {
        return LocalFileLister("")
    }

    private fun yandexDiskFileLister(): YandexDiskFileLister {
        return YandexDiskFileLister(
            authToken = yandexAuthToken!!,
            YandexDiskCloudReader(yandexAuthToken!!)
        )
    }

    private fun showError(errorMsg: String) {
        lifecycleScope.launch (Dispatchers.Main) {
            binding.errorView.apply {
                text = errorMsg
                visibility = View.VISIBLE
            }
        }
    }

    private fun hideError() {
        binding.errorView.apply {
            text = null
            visibility = View.GONE
        }
    }

    private fun onYandexSelectButtonLongClicked(): Boolean {

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.DIALOG_title_logout_from_yandex)
            .setMessage(R.string.DIALOG_message_yandex_auth_token_will_be_cleared)
            .setPositiveButton(R.string.DIALOG_button_yes) { _,_ ->
                yandexAuthToken = null
                storeYandexAuthToken()
            }
            .setNegativeButton(R.string.DIALOG_button_no) { _,_ -> }
            .create()
            .show()

        return true
    }

    private fun onLocalSelectButtonClicked() {
        storageType = StorageType.LOCAL
        fileSelector = localFileSelector()

        storageAccessHelper.requestFullAccess { isGranted ->
            if (isGranted) showFileSelector()
            else showToast(R.string.there_is_no_reading_access)
        }
    }

    private fun onYandexSelectButtonClicked() {

        if (null == yandexAuthToken) {
            startYandexAuth()
            return
        }

        storageType = StorageType.YANDEX_DISK
        fileSelector = yandexFileSelector()
        showFileSelector()
    }

    private fun showFileSelector() {
        fileSelector?.show(childFragmentManager, FileSelector.TAG)
            ?: showToast(getString(R.string.ERROR_unknown_storage_type, storageType))
    }

    /*private fun fileSelector(): FileSelectorFragment<SimpleSortingMode>? {
        return when(storageType) {
            StorageType.LOCAL -> localFileSelector()
            StorageType.YANDEX_DISK -> yandexFileSelector()
            else -> null
        }
    }*/

    private fun startYandexAuth() {
        yandexAuthLauncher.launch(YandexAuthLoginOptions(LoginType.WEBVIEW))
    }

    private fun yandexFileSelector(): FileSelector<SimpleSortingMode> {
        return YandexDiskFileSelector.create(
            fragmentResultKey = YANDEX_DISK_SELECTION_REQUEST_KEY,
            authToken = yandexAuthToken!!,
            isDirSelectionMode = isDirSelectionMode(),
            isMultipleSelectionMode = isMultipleSelectionMode()
        )
    }

    private fun localFileSelector(): FileSelector<SimpleSortingMode> {
        return LocalFileSelector.create(
            fragmentResultKey = LOCAL_SELECTION_REQUEST_KEY,
            isDirSelectionMode = isDirSelectionMode(),
            isMultipleSelectionMode = isMultipleSelectionMode(),
        )
    }
    private fun isDirSelectionMode(): Boolean = binding.dirModeSwitch.isChecked

    private fun isMultipleSelectionMode(): Boolean = binding.multipleModeSwitch.isChecked

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        storeYandexAuthToken()
    }

    private fun restoreString(key: String): String? {
        return PreferenceManager.getDefaultSharedPreferences(requireContext()).getString(key, null)
    }

    private fun storeYandexAuthToken() {
        storeString(YANDEX_AUTH_TOKEN, yandexAuthToken)
    }

    private fun storeString(key: String, value: String?) {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .edit().putString(key, value).apply()
    }

    private fun prepareYandexAuthLauncher() {

        val yandexAuthOptions by lazy {
            YandexAuthOptions(requireContext(), true)
        }

        val yandexAuthSdkContract by lazy {
            YandexAuthSdkContract(yandexAuthOptions)
        }

        yandexAuthLauncher = registerForActivityResult(yandexAuthSdkContract) { result: YandexAuthResult ->

            when(result) {
                is YandexAuthResult.Success -> {
                    yandexAuthToken = result.token.value
                    storeYandexAuthToken()
//                prepareYandexFileSelector()
                    showFileSelector()
                }
                is YandexAuthResult.Failure -> {
                    Log.e(TAG, result.exception.message ?: result.exception.javaClass.name)
                }
                else -> {
                    Log.w(TAG, "Auth was cancelled.")
                }
            }
        }
    }

    companion object {
        val TAG: String = DemoFragment::class.java.simpleName

        fun create(): DemoFragment = DemoFragment()

        const val YANDEX_AUTH_TOKEN = "YANDEX_AUTH_TOKEN"

        const val LOCAL_SELECTION_REQUEST_KEY = "LOCAL_SELECTION_REQUEST_CODE"
        const val YANDEX_DISK_SELECTION_REQUEST_KEY = "YANDEX_DISK_SELECTION_REQUEST_CODE"
    }
}

private fun ProgressBar.visible() {
    visibility = View.VISIBLE
}

private fun ProgressBar.gone() {
    visibility = View.GONE
}

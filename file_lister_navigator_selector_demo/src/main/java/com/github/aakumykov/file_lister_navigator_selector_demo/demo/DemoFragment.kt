package com.github.aakumykov.file_lister_navigator_selector_demo.fragments.demo

import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.preference.PreferenceManager
import com.github.aakumykov.file_lister_navigator_selector.extensions.listenForFragmentResult
import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode
import com.github.aakumykov.file_lister_navigator_selector.file_selector.FileSelectorFragment
//import com.github.aakumykov.file_lister_navigator_selector.extensions.listenForFragmentResult
//import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode
//import com.github.aakumykov.file_lister_navigator_selector.file_selector.FileSelectorFragment
//import com.github.aakumykov.file_lister_navigator_selector.local_file_selector.LocalFileSelectorFragment
import com.github.aakumykov.file_lister_navigator_selector_demo.R
import com.github.aakumykov.file_lister_navigator_selector_demo.databinding.FragmentDemoBinding
import com.github.aakumykov.file_lister_navigator_selector_demo.extensions.showToast
import com.github.aakumykov.local_file_lister_navigator_selector.local_file_selector.LocalFileSelectorFragment
import com.github.aakumykov.storage_access_helper.StorageAccessHelper
import com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_file_selector.YandexDiskFileSelectorFragment
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthSdkContract
import com.yandex.authsdk.internal.strategy.LoginType

class DemoFragment : Fragment(R.layout.fragment_demo), FragmentResultListener {

    private var _binding: FragmentDemoBinding? = null
    private val binding get()= _binding!!

    private var fileSelector: FileSelectorFragment<SimpleSortingMode>? = null

    private var yandexAuthToken: String? = null
    private lateinit var yandexAuthLauncher: ActivityResultLauncher<YandexAuthLoginOptions>

    private lateinit var storageAccessHelper: StorageAccessHelper

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
        FileSelectorFragment.extractSelectionResult(result)?.also { list ->
            binding.selectionResultView.text = getString(
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

        prepareYandexFileSelector()
        showFileSelector()
    }

    private fun prepareYandexFileSelector() {
        fileSelector = yandexFileSelector()
    }

    private fun showFileSelector() {
        fileSelector?.show(childFragmentManager, FileSelectorFragment.TAG)
    }

    private fun startYandexAuth() {
        yandexAuthLauncher.launch(YandexAuthLoginOptions(LoginType.WEBVIEW))
    }

    private fun yandexFileSelector(): FileSelectorFragment<SimpleSortingMode> {
        return YandexDiskFileSelectorFragment.create(
            fragmentResultKey = YANDEX_DISK_SELECTION_REQUEST_KEY,
            authToken = yandexAuthToken!!,
            isDirSelectionMode = isDirSelectionMode(),
            isMultipleSelectionMode = isMultipleSelectionMode()
        )
    }

    private fun localFileSelector(): FileSelectorFragment<SimpleSortingMode> {
        return LocalFileSelectorFragment.create(
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

        yandexAuthLauncher = registerForActivityResult(yandexAuthSdkContract) { result ->
            /*yandexAuthToken = result.getOrNull()?.value
            storeYandexAuthToken()
            if (null != yandexAuthToken)
                showFileSelector()*/

            result.getOrNull()?.value?.also { token ->
                yandexAuthToken = token
                storeYandexAuthToken()
                prepareYandexFileSelector()
                showFileSelector()
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
package com.github.aakumykov.file_lister_navigator_selector_2_demo

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.github.aakumykov.cloud_authenticator.CloudAuthenticator
import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode
import com.github.aakumykov.file_lister_navigator_selector.file_selector.FileSelector
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.file_lister_navigator_selector_2_demo.databinding.FragmentSimpleDemoBinding
import com.github.aakumykov.file_lister_navigator_selector_2_demo.enums.WorkMode
import com.github.aakumykov.file_lister_navigator_selector_2_demo.extensions.eraseStringFromPreferences
import com.github.aakumykov.file_lister_navigator_selector_2_demo.extensions.errorMsg
import com.github.aakumykov.file_lister_navigator_selector_2_demo.extensions.getStringFromPreferences
import com.github.aakumykov.file_lister_navigator_selector_2_demo.extensions.showToast
import com.github.aakumykov.file_lister_navigator_selector_2_demo.extensions.storeStringInPreferences
import com.github.aakumykov.local_file_lister_navigator_selector.local_file_selector.LocalFileSelector
import com.github.aakumykov.storage_access_helper.StorageAccessHelper
import com.github.aakumykov.yandex_authenticator.YandexAuthenticator
import com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_file_selector.YandexDiskFileSelector


class SimpleDemoFragment():
    Fragment(R.layout.fragment_simple_demo),
    FileSelector.Callbacks,
        CloudAuthenticator.Callbacks
{
    private val sortingMode: SimpleSortingMode get() {
        return when(binding.sortingModeSelector.checkedRadioButtonId) {
            R.id.sortingModeBySize -> SimpleSortingMode.SIZE
            R.id.sortingModeByMTime -> SimpleSortingMode.M_TIME
            else -> SimpleSortingMode.NAME
        }
    }
    private val isMultipleSelectionMode: Boolean get() = binding.multipleSelectionMode.isChecked
    private val isDirectoriesOnlyMode: Boolean get() = binding.directoriesOnly.isChecked

    private val authToken: String? get() = getStringFromPreferences(YANDEX_AUTH_TOKEN)
    private val hasAuth: Boolean get() = null != authToken

    private val storageAccessHelper: StorageAccessHelper by lazy {
        StorageAccessHelper.create(this)
    }

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private val yandexAuthenticator: CloudAuthenticator by lazy {
        YandexAuthenticator(this)
            .prepare(
                context = requireContext(),
                loginType = CloudAuthenticator.LoginType.NATIVE,
                activityResultLauncher = activityResultLauncher
            )
    }

    override fun onCloudAuthSuccess(authToken: String) {
        super.onCloudAuthSuccess(authToken)
        storeStringInPreferences(YANDEX_AUTH_TOKEN, authToken)
        displayAuthState()
    }

    override fun onCloudAuthFailed(throwable: Throwable) {
        super.onCloudAuthFailed(throwable)
        showError(throwable)
    }

    override fun onCloudAuthCancelled() {
        super.onCloudAuthCancelled()
        showToast(R.string.auth_cancelled)
    }

    override fun onFileSelected(list: List<FSItem>) {
        showInfo(list.joinToString { it.name + "\n" })
    }

    private fun showInfo(text: String) {
        binding.infoView.apply {
            this.text = text
            binding.infoView.setTextColor(resources.getColor(android.R.color.tab_indicator_text))
        }
    }

    private fun showError(throwable: Throwable) {
        showError(throwable.errorMsg)
        Log.e(TAG, throwable.errorMsg, throwable)
    }

    private fun showError(message: String) {
        binding.infoView.apply {
            text = message
            setTextColor(resources.getColor(R.color.error))
        }
    }

    private val workMode: WorkMode
        get() {
        return when(binding.workModeSelector.checkedRadioButtonId) {
            R.id.workModeYandexDisk -> WorkMode.YANDEX
            else -> WorkMode.LOCAL
        }
    }

    private var _binding: FragmentSimpleDemoBinding? = null
    private val binding get()= _binding!!

    private val fileSelector: FileSelector<SimpleSortingMode>
        get() {
        return when(workMode) {
            WorkMode.YANDEX -> createAndPrepareYandexSelector()
            else -> createAndPrepareLocalSelector()
        }
    }

    private val isReverseOrder: Boolean get() = binding.reverseOrder.isChecked
    private val isFoldersFirst: Boolean get() = binding.foldersFirst.isChecked

    private fun createAndPrepareLocalSelector(): FileSelector<SimpleSortingMode> {
        return LocalFileSelector(
            initialSortingMode = sortingMode,
            initialReverseOrder = isReverseOrder,
            initialFoldersFirst = isFoldersFirst,
            isDirSelectionMode = isDirectoriesOnlyMode,
            isMultipleSelectionMode = isMultipleSelectionMode
        ).prepare()
    }

    private fun createAndPrepareYandexSelector(): FileSelector<SimpleSortingMode> {
        return YandexDiskFileSelector().prepare(authToken = authToken!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSimpleDemoBinding.bind(view)

        binding.selectFileButton.setOnClickListener { onSelectFileClicked() }
        binding.yandexAuthButton.setOnClickListener { onYandexAuthButtonClicked() }

        storageAccessHelper.prepareForReadAccess()

        activityResultLauncher = registerForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            callback = { activityResult ->
                yandexAuthenticator.parseResult(activityResult)
            }
        )

        displayWorkMode()
        displayAuthState()
    }

    override fun onResume() {
        super.onResume()
        FileSelector.restoreConnection(this, this)
    }

    private fun onYandexAuthButtonClicked() {
        if (hasAuth) {
            eraseStringFromPreferences(YANDEX_AUTH_TOKEN)
            displayAuthState()
        }
        else yandexAuthenticator.startAuth(requireContext())
    }

    private fun onSelectFileClicked() {
        if (WorkMode.LOCAL == workMode) {
            storageAccessHelper.requestReadAccess { startSelectingFile() }
        } else {
            if (hasAuth) startSelectingFile()
            else showToast(R.string.auth_required)
        }
    }

    private fun startSelectingFile() {
        fileSelector.display(this, this)
    }

    private fun displayWorkMode() {
        showInfo(workMode.name)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val currentTheme get() = requireContext().theme

    private fun displayAuthState() {

        /*val primaryColor = TypedValue().let {
            currentTheme.resolveAttribute(android.R.attr.colorPrimary, it, true)
            it.data
        }*/


        if (hasAuth) {
            binding.yandexAuthButton.apply {
                setText(R.string.yandex_deauth)
                backgroundTintList = ColorStateList.valueOf(
                    resources.getColor(R.color.auth_yes, currentTheme)
                )
            }
        } else {
            binding.yandexAuthButton.apply {
                setText(R.string.yandex_auth)
                backgroundTintList = ColorStateList.valueOf(
                    resources.getColor(R.color.auth_no, currentTheme)
                )
            }
        }
    }

    companion object {
        val TAG: String = SimpleDemoFragment::class.java.simpleName
        const val YANDEX_AUTH_TOKEN = "YANDEX_AUTH_TOKEN"
        fun create(): SimpleDemoFragment {
            return SimpleDemoFragment()
        }
    }
}
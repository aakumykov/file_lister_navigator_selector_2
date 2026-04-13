package com.github.aakumykov.demo

import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.aakumykov.demo.databinding.FragmentSimpleDemoBinding
import com.github.aakumykov.demo.enums.WorkMode
import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode
import com.github.aakumykov.file_lister_navigator_selector.file_selector.FileSelector
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.local_file_lister_navigator_selector.local_file_selector.LocalFileSelector
import com.github.aakumykov.storage_access_helper.StorageAccessHelper
import com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_file_selector.YandexDiskFileSelector
import kotlin.collections.joinToString

class SimpleDemoFragment():
    Fragment(R.layout.fragment_simple_demo),
    FileSelector.Callbacks
{
    private val multipleSelectionMode: Boolean get() = binding.multipleSelectionMode.isChecked
    private val directoriesOnlyMode: Boolean get() = binding.directoriesOnly.isChecked

    private val storageAccessHelper: StorageAccessHelper by lazy {
        StorageAccessHelper.create(this)
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

    private fun createAndPrepareLocalSelector(): FileSelector<SimpleSortingMode> {
        return LocalFileSelector().prepare(
            isDirSelectionMode = directoriesOnlyMode,
            isMultipleSelectionMode = multipleSelectionMode,
        )
    }

    private fun createAndPrepareYandexSelector(): FileSelector<SimpleSortingMode> {
        return YandexDiskFileSelector().prepare(
            authToken = "",
            initialPath = "/",
            isDirSelectionMode = directoriesOnlyMode,
            isMultipleSelectionMode = multipleSelectionMode,
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSimpleDemoBinding.bind(view)

        binding.workModeSelector.setOnCheckedChangeListener(::onWorkModeChanged)
        displayWorkMode()

        binding.selectFileButton.setOnClickListener(::onSelectFileClicked)

        storageAccessHelper.prepareForReadAccess()
    }

    override fun onResume() {
        super.onResume()
        FileSelector.Companion.restoreConnection(this, this)
    }

    private fun onSelectFileClicked(view: View) {
        if (WorkMode.LOCAL == workMode) {
            storageAccessHelper.requestReadAccess { startSelectingFile() }
        } else { startSelectingFile() }
    }

    private fun startSelectingFile() {
        fileSelector.show(this, this)
    }

    private fun displayWorkMode() {
        showInfo(workMode.name)

    }

    private fun onWorkModeChanged(radioGroup: RadioGroup, selectedId: Int) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG: String = SimpleDemoFragment::class.java.simpleName
        fun create(): SimpleDemoFragment {
            return SimpleDemoFragment()
        }
    }
}
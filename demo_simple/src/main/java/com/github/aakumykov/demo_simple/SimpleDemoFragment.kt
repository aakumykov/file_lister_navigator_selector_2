package com.github.aakumykov.demo_simple

import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.github.aakumykov.demo_simple.databinding.FragmentSimpleDemoBinding
import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode
import com.github.aakumykov.file_lister_navigator_selector.file_selector.ConnectableFileSelector
import com.github.aakumykov.file_lister_navigator_selector.file_selector.FileSelector
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.local_file_lister_navigator_selector.local_file_selector.LocalFileSelector
import com.github.aakumykov.yandex_disk_file_lister_navigator_selector.yandex_disk_file_selector.YandexDiskFileSelector

class SimpleDemoFragment():
    Fragment(R.layout.fragment_simple_demo),
    FileSelector.Callbacks
{
    override fun onFileSelected(list: List<FSItem>) {
        showInfo(list.joinToString { it.name + "\n" })
    }

    private fun showInfo(text: String) {
        binding.infoView.apply {
            this.text = text
            setTextColor(resources.getColor(android.R.color.tab_indicator_text))
        }
    }

    private fun showError(message: String) {
        binding.infoView.apply {
            text = message
            setTextColor(resources.getColor(R.color.error))
        }
    }

    private val workMode: WorkMode get() {
        return when(binding.workModeSelector.checkedRadioButtonId) {
            R.id.workModeYandexDisk -> WorkMode.YANDEX
            else -> WorkMode.LOCAL
        }
    }

    private var _binding: FragmentSimpleDemoBinding? = null
    private val binding get()= _binding!!

    private val fileSelector: FileSelector<SimpleSortingMode> get() {
        return when(workMode) {
            WorkMode.YANDEX -> createAndPrepareYandexSelector()
            else -> createAndPrepareLocalSelector()
        }
    }

    private fun createAndPrepareLocalSelector(): FileSelector<SimpleSortingMode> {
        return LocalFileSelector().prepare(
            callbacks = this,
            isDirSelectionMode = false,
            isMultipleSelectionMode = false,
        )
    }

    private fun createAndPrepareYandexSelector(): FileSelector<SimpleSortingMode> {
        return YandexDiskFileSelector().prepare(
            callbacks = this,
            authToken = "",
            initialPath = "/",
            isDirSelectionMode = false,
            isMultipleSelectionMode = false,
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSimpleDemoBinding.bind(view)

        binding.workModeSelector.setOnCheckedChangeListener(::onWorkModeChanged)
        displayWorkMode()

        binding.selectFileButton.setOnClickListener(::onSelectFileClicked)
    }

    override fun onResume() {
        super.onResume()
        connectToFileSelector()
    }

    private fun connectToFileSelector() {
        val fragment = childFragmentManager.findFragmentByTag(FileSelector.TAG)
        if (fragment is ConnectableFileSelector) {
            fragment.connect(this, this)
        }
    }

    private fun onSelectFileClicked(view: View) {
        fileSelector.show(this).also {
            connectToFileSelector()
        }
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

package com.github.aakumykov.file_lister_navigator_selector_2_demo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode
import com.github.aakumykov.file_lister_navigator_selector.file_selector.FileSelector
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.file_lister_navigator_selector_2_demo.databinding.ActivitySimpleDemoBinding
import com.github.aakumykov.local_file_lister_navigator_selector.local_file_selector.LocalFileSelector

class SimpleDemoActivity : AppCompatActivity(), FileSelector.Callbacks {

    private lateinit var binding: ActivitySimpleDemoBinding
    private val isMultipleSelectionMode: Boolean get() = binding.multipleSelectionMode.isChecked

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivitySimpleDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.selectFileButton.setOnClickListener { onSelectFileClicked() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (null == savedInstanceState) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainerView, SimpleDemoFragment.Companion.create(), SimpleDemoFragment.Companion.TAG)
                .commit()
        }
    }


    fun onSelectFileClicked() {
        LocalFileSelector()
            .prepare(
                isMultipleSelectionMode = isMultipleSelectionMode
            ).display(this, this)
    }

    override fun onFileSelected(list: List<FSItem>) {
        binding.infoView.text = list.joinToString(",\n") { it.name }
    }
}
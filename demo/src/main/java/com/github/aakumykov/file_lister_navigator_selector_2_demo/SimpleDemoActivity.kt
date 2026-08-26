package com.github.aakumykov.file_lister_navigator_selector_2_demo

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.aakumykov.file_lister_navigator_selector.file_lister.SimpleSortingMode
import com.github.aakumykov.file_lister_navigator_selector.file_selector.FileSelector
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.file_lister_navigator_selector_2_demo.databinding.ActivitySimpleDemoBinding
import com.github.aakumykov.file_lister_navigator_selector_2_demo.extensions.showToast
import com.github.aakumykov.local_file_lister_navigator_selector.local_file_selector.LocalFileSelector
import permissions.dispatcher.ktx.PermissionsRequester
import permissions.dispatcher.ktx.constructPermissionsRequest

class SimpleDemoActivity : AppCompatActivity(), FileSelector.Callbacks {

    private lateinit var binding: ActivitySimpleDemoBinding
    private val isMultipleSelectionMode: Boolean get() = binding.multipleSelectionMode.isChecked

    private lateinit var storagePermissionsRequester: PermissionsRequester

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivitySimpleDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storagePermissionsRequester = constructPermissionsRequest(
            * storageAccessPermissions,
            requiresPermission = ::onStorageAccessAllowed,
            onPermissionDenied = ::onStorageAccessDenied,
            onNeverAskAgain = ::onStorageAccessNeverAskAgain
        )

        binding.requestPermissionButton.setOnClickListener { storagePermissionsRequester.launch() }
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

    private fun onStorageAccessAllowed() {
        showToast("Доступ к хранилищу разрешён")
    }

    private fun onStorageAccessDenied() {
        showToast("Доступ к хранилищу отклонён")
    }

    private fun onStorageAccessNeverAskAgain() {
        showToast("Доступ к хранилищу заприщон")
    }

    private val storageAccessPermissions: Array<String> get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            arrayOf(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        } else {
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )
        }
    }
}
package com.github.aakumykov.file_lister_navigator_selector.file_selector

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.github.aakumykov.file_lister_navigator_selector.FileListAdapter
import com.github.aakumykov.file_lister_navigator_selector.R
import com.github.aakumykov.file_lister_navigator_selector.StorageLister
import com.github.aakumykov.file_lister_navigator_selector.databinding.DialogFileSelectorBinding
import com.github.aakumykov.file_lister_navigator_selector.dir_creator_dialog.DirCreatorDialog
import com.github.aakumykov.file_lister_navigator_selector.entities.Storage
import com.github.aakumykov.file_lister_navigator_selector.extensions.hide
import com.github.aakumykov.file_lister_navigator_selector.extensions.show
import com.github.aakumykov.file_lister_navigator_selector.extensions.showIf
import com.github.aakumykov.file_lister_navigator_selector.file_explorer.FileExplorer
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.file_lister_navigator_selector.fs_item.SimpleFSItem
import com.github.aakumykov.file_lister_navigator_selector.sorting_info_supplier.SortingInfoSupplier
import com.github.aakumykov.file_lister_navigator_selector.sorting_mode_translator.SortingModeTranslator
import com.github.aakumykov.file_lister_navigator_selector.utils.ListHoldingListAdapter
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import com.google.gson.Gson

// TODO: Сделать интерфейс "FileSelectorFragment" ?

abstract class FileSelector<SortingModeType> : DialogFragment(R.layout.dialog_file_selector),
    AdapterView.OnItemClickListener,
    AdapterView.OnItemLongClickListener,
    AdapterView.OnItemSelectedListener
{
    private var _binding: DialogFileSelectorBinding? = null
    private val binding get() = _binding!!

    private var sortingDialog: AlertDialog? = null

    private lateinit var storageSpinnerAdapter: StorageListAdapter
    private lateinit var filesListAdapter: FileListAdapter<SortingModeType>

    private val viewModel: FileSelectorViewModel<SortingModeType> by viewModels {
        FileSelectorViewModel.Factory(
            createFileExplorer(),
            createStorageLister(),
            isMultipleSelectionMode()
        )
    }

    private fun createStorageLister(): StorageLister {
        return StorageLister(
            requireContext().applicationContext,
            R.string.internal_storage_title,
            R.string.external_storage_title
        )
    }

    private val gson by lazy { Gson() }


    // Методы, просто возвращающие значение, имеют приставку "get".
    protected abstract fun getDefaultInitialPath(): String
    protected abstract fun getDefaultDirSelectionMode(): Boolean
    protected abstract fun getDefaultMultipleSelectionMode(): Boolean

    @Deprecated("Оцени обоснованность этого метода")
    protected abstract fun defaultSortingMode(): SortingModeType

    @Deprecated("Оцени обоснованность этого метода")
    protected abstract fun defaultReverseMode(): Boolean


    // Методы, создающие новый экземпляр, имеют приставку "create".
    protected abstract fun createFileExplorer(): FileExplorer<SortingModeType>
    protected abstract fun createDirCreatorDialog(basePath: String): DirCreatorDialog
    protected abstract fun createSortingModeTranslator(): SortingModeTranslator<SortingModeType>
    protected abstract fun createSortingInfoSupplier(): SortingInfoSupplier<SortingModeType>


    // Методы, не возвращающие значений, не имеют приставки-действия.
    protected abstract fun requestWriteAccess(
        onWriteAccessGranted: () -> Unit,
        onWriteAccessRejected: (errorMsg: String?) -> Unit
    )


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = DialogFileSelectorBinding.bind(view)

        // TODO: инкапсулировать в диалоге создания каталога
        childFragmentManager.setFragmentResultListener(DirCreatorDialog.DIR_NAME, viewLifecycleOwner, ::onDirCreationResult)

        prepareListAdapter()
        prepareStorageSelector()
        prepareButtons()
        subscribeToViewModel()

        if (null == savedInstanceState)
            viewModel.startWork()
    }

    private fun prepareStorageSelector() {

        storageSpinnerAdapter = StorageListAdapter()

        binding.storageSelectorSpinner.apply {
            onItemSelectedListener = this@FileSelector
            adapter = storageSpinnerAdapter
        }
    }



    private fun subscribeToViewModel() {
        viewModel.storageList.observe(viewLifecycleOwner, ::onStorageListChanged)
        viewModel.selectedStorage.observe(viewLifecycleOwner, ::onSelectedStorageChanged)

        viewModel.path.observe(viewLifecycleOwner, ::onPathChanged)
        viewModel.list.observe(viewLifecycleOwner, ::onListChanged)
        viewModel.selectedList.observe(viewLifecycleOwner, ::onSelectedListChanged)
        viewModel.errorMsg.observe(viewLifecycleOwner, ::onNewError)
        viewModel.isBusy.observe(viewLifecycleOwner, ::onIsBusyChanged)
    }

    private fun prepareButtons() {
        binding.confirmSelectionButton.setOnClickListener { onConfirmSelectionClicked() }
        binding.dialogCloseButton.setOnClickListener { dismiss() }
        binding.createDirButton.setOnClickListener { onCreateDirClicked() }
        binding.sortButton.setOnClickListener { onSortButtonClicked() }
        binding.backButton.setOnClickListener { onBackButtonClicked() }
        binding.refreshButton.setOnClickListener { onRefreshRequested() }
    }

    private fun onRefreshRequested() {
        viewModel.reopenCurrentDir()
    }

    private fun onBackButtonClicked() {
        viewModel.onBackClicked()
    }

    private fun prepareListAdapter() {

        filesListAdapter = FileListAdapter(
            requireContext(),
            R.layout.file_list_item,
            R.id.titleView,
            R.id.infoView,
            R.id.iconView,
            createSortingInfoSupplier(),
            viewModel.currentSortingMode)

        binding.listView.adapter = filesListAdapter

        binding.listView.onItemClickListener = this
        binding.listView.onItemLongClickListener = this
    }


    private fun onStorageListChanged(list: List<Storage>?) {
        Log.d(TAG, "onStorageListChanged() called with: list = $list")
        list?.also {
            storageSpinnerAdapter.setList(list)
        }
    }


    private fun onSelectedStorageChanged(storage: Storage?) {
        Log.d(TAG, "onSelectedStorageChanged() called with: storage = $storage")
        /*storage?.also {
            viewModel.changeSelectedStorage(storage)
        }*/
    }


    private fun onPathChanged(s: String?) {
        binding.pathView.text = s
    }

    private fun onListChanged(list: List<FSItem>?) {
        list?.also {
            filesListAdapter.setList(it)
        }.also {
            if (0 == list?.size)
                binding.emptyListLabel.visibility = View.VISIBLE
            else
                binding.emptyListLabel.visibility = View.GONE
        }
    }

    private fun onSelectedListChanged(selectedItemsList: List<FSItem>?) {
        selectedItemsList?.also {
            filesListAdapter.updateSelections(selectedItemsList)
            binding.confirmSelectionButton.isEnabled = (selectedItemsList.size > 0)
        }
    }

    private fun onNewError(throwable: Throwable?) {
        throwable?.also {
            binding.errorView.apply {
                text = getString(R.string.error, ExceptionUtils.getErrorMessage(throwable))
                this.show()
            }
        }
    }

    private fun onIsBusyChanged(isBusy: Boolean?) {
        isBusy?.also {
            binding.progressBar.showIf { isBusy }
            if (isBusy)
                binding.errorView.hide()
        }
    }

    private fun onCreateDirClicked() {
        requestWriteAccess(
            onWriteAccessGranted = {
                createDirCreatorDialog(viewModel.fileExplorer.getCurrentPath())
                    .show(childFragmentManager, DirCreatorDialog.TAG)
            },
            onWriteAccessRejected = {
                Toast.makeText(requireContext(), R.string.write_access_denied, Toast.LENGTH_SHORT).show()
            }
        )
    }


    private fun onSortButtonClicked() {
        showSortingDialog()
    }

    private fun showSortingDialog() {

        val sortingFlagsView = layoutInflater.inflate(R.layout.sorting_flags_dialog_view, null)
            .apply {
                findViewById<CheckBox>(R.id.foldersFirstCheckbox).apply {
                    isChecked = viewModel.isFoldersFirst
                    setOnCheckedChangeListener { dialog, isChecked ->
                        onFoldersFirstChanged(isChecked)
                        sortingDialog?.dismiss()
                    }
                }
            }

        sortingDialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.SORTING_MODE_DIALOG_title)
            .setView(sortingFlagsView)
            .setSingleChoiceItems(
                createSortingModeTranslator().sortingModeNames(viewModel.currentSortingMode, viewModel.isReverseOrder),
                createSortingModeTranslator().sortingModeToPosition(viewModel.currentSortingMode)
            ) { dialog, position ->
                onSortingModeChanged(createSortingModeTranslator().positionToSortingMode(position))
                dialog.dismiss()
            }
            .create()

        sortingDialog?.show()
    }

    private fun onFoldersFirstChanged(isFoldersFirst: Boolean) {
        viewModel.changeFoldersFist(isFoldersFirst)
    }

    private fun onSortingModeChanged(sortingMode: SortingModeType) {
        viewModel.changeSortingMode(sortingMode)
        filesListAdapter.changeSortingMode(sortingMode)
    }

    private fun onConfirmSelectionClicked() {
        setSelectionResult(selectedItemsToBundle())
        dismiss()
    }

    /**
     * Возвращает результат по механизму FragmentResultAPI (вызывает "setFragmentResult"),
     * используя специфический для реализации ключ результата.
     */
    private fun setSelectionResult(bundle: Bundle) {
        getFragmentResultKey()?.also { key ->
            parentFragmentManager.setFragmentResult(key, bundle)
        } ?: throw IllegalStateException("Key for use to result selection result with FragmentResultAPI not found.")
    }

    private fun selectedItemsToBundle(): Bundle {
        val listOfJSON = viewModel.selectedList.value?.map {
            gson.toJson(it, SimpleFSItem::class.java)
        }
        return bundleOf(SELECTED_ITEMS_LIST to listOfJSON)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        viewModel.onItemClick(position)
    }

    override fun onItemLongClick(
        parent: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long
    ): Boolean {
        viewModel.onItemLongClick(position)
        return true
    }


    // Для выпадающего списка выборщика хранилища
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//        viewModel.storageList.value?.get(position)?.also { storage ->
//            viewModel.changeSelectedStorage(storage)
//        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
//        binding.storageSelectorSpinner.prompt = getString(R.string.storage_selector_spinner_prompt)
    }


    private fun onDirCreationResult(requestKey: String, resultBundle: Bundle) {
        resultBundle.getString(DirCreatorDialog.DIR_NAME)?.also {
            Toast.makeText(requireContext(), getString(R.string.dir_was_created, it), Toast.LENGTH_SHORT).show()
        }
        viewModel.reopenCurrentDir()
    }


    protected fun initialPath(): String {
        return arguments?.getString(INITIAL_PATH) ?: getDefaultInitialPath()
    }

    protected fun isDirMode(): Boolean {
        return arguments?.getBoolean(DIR_SELECTION_MODE) ?: getDefaultDirSelectionMode()
    }

    private fun isMultipleSelectionMode(): Boolean {
        return arguments?.getBoolean(MULTIPLE_SELECTION_MODE) ?: getDefaultMultipleSelectionMode()
    }

    private fun getFragmentResultKey(): String? {
        return arguments?.getString(FRAGMENT_RESULT_KEY)
    }


    companion object {
        val TAG: String = FileSelector::class.java.simpleName

        //
        // Ключ для передачи фрагменту ключа же, по которому он вернёт результат (через FragmentResultAPI).
        //
        const val FRAGMENT_RESULT_KEY = "FRAGMENT_RESULT_KEY"

        const val SELECTED_ITEMS_LIST = "SELECTED_ITEMS_LIST"

        @Deprecated("Перенести в реализацию для Яндекс")
        const val AUTH_TOKEN = "AUTH_TOKEN"

        const val INITIAL_PATH = "INITIAL_PATH"
        const val DIR_SELECTION_MODE = "DIR_SELECTION_MODE"
        const val MULTIPLE_SELECTION_MODE = "MULTIPLE_SELECTION_MODE"

        fun extractSelectionResult(result: Bundle): List<FSItem>? {
            val gson = Gson()
            return result.getStringArrayList(SELECTED_ITEMS_LIST)
                ?.map { json ->
                    gson.fromJson(json, SimpleFSItem::class.java)
                }
        }
    }
}

package com.github.aakumykov.file_lister_navigator_selector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.DimenRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem;
import com.github.aakumykov.file_lister_navigator_selector.sorting_info_supplier.SortingInfoSupplier;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

// FIXME: зависимость от внешнего класса FSItem вызывает у меня сомнения (инкапсуляция)
public class FileListAdapter<SortingModeType> extends ArrayAdapter<FSItem> {

    public static String TAG = FileListAdapter.class.getSimpleName();

    private final static String defaultFolderGraphicalCharacter = "\uD83D\uDCC1";
    private final static String defaultFileGraphicalCharacter = "\uD83D\uDCC4";

    private final Context context;
    private final LayoutInflater inflater;
    private final int layoutResource;
    private final int titleViewId;
    private final int checkBoxId;
    private final int infoViewId;
    private final int fileIconViewId;
    private final List<FSItem> selectionsList = new ArrayList<>();
    private final String folderGraphicalCharacter;
    private final String fileGraphicalCharacter;
    private final SortingInfoSupplier<SortingModeType> sortingInfoSupplier;

    private SortingModeType sortingMode;

    private final boolean isMultipleChoice;

    public FileListAdapter(Context context,
                           @LayoutRes int layoutResource,
                           @IdRes int checkBoxId,
                           @IdRes int titleViewId,
                           @IdRes int infoViewId,
                           @IdRes int fileIconViewId,
                           SortingInfoSupplier<SortingModeType> sortingInfoSupplier,
                           SortingModeType initialSortingMode,
                           boolean isMultipleChoice
    ) {
        this(context,
                layoutResource,
                checkBoxId,
                titleViewId,
                infoViewId,
                fileIconViewId,
                defaultFolderGraphicalCharacter,
                defaultFileGraphicalCharacter,
                sortingInfoSupplier,
                initialSortingMode,
                isMultipleChoice
        );
    }

    public FileListAdapter(Context context,
                           @LayoutRes int layoutResource,
                           @IdRes int checkBoxId,
                           @IdRes int titleViewId,
                           @IdRes int infoViewId,
                           @IdRes int fileIconViewId,
                           String folderGraphicalCharacter,
                           String fileGraphicalCharacter,
                           SortingInfoSupplier<SortingModeType> sortingInfoSupplier,
                           SortingModeType initialSortingMode,
                           boolean isMultipleChoice
    ) {
        super(context, layoutResource, new ArrayList<>());
        this.context = context;
        this.layoutResource = layoutResource;
        this.checkBoxId = checkBoxId;
        this.titleViewId = titleViewId;
        this.infoViewId = infoViewId;
        this.fileIconViewId = fileIconViewId;
        this.inflater = LayoutInflater.from(context);
        this.folderGraphicalCharacter = folderGraphicalCharacter;
        this.fileGraphicalCharacter = fileGraphicalCharacter;
        this.sortingInfoSupplier = sortingInfoSupplier;
        this.sortingMode = initialSortingMode;
        this.isMultipleChoice = isMultipleChoice;
    }


    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;
        if(convertView==null){
            convertView = inflater.inflate(this.layoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        FSItem fsItem = super.getItem(position);

        if (null == fsItem)
            throw new IllegalStateException("FSItem is null");

        fillViewHolder(viewHolder, fsItem);

        return convertView;
    }

    private void fillViewHolder(ViewHolder viewHolder, FSItem fsItem) {

        String title = fsItem.getName();

        String fileInfo = "";
        if (!fsItem.isDir())
            fileInfo = sortingInfoSupplier.getSortingInfo(context, fsItem, sortingMode, "", "");

        if (selectionsList.contains(fsItem))
            title = "*" + " " + title;

        String icon = fileGraphicalCharacter;
        if (fsItem.isDir())
            icon = folderGraphicalCharacter;

        viewHolder.titleView.setText(title);
        viewHolder.infoView.setText(fileInfo);
        viewHolder.iconView.setText(icon);

        if (fileInfo.isEmpty()) {
            viewHolder.infoView.setVisibility(View.GONE);
            viewHolder.titleView.setPadding(
                    getDimension(R.dimen.item_title_padding_horizontal),
                    getDimension(R.dimen.item_title_padding_vertical_simple_mode),
                    getDimension(R.dimen.item_title_padding_horizontal),
                    getDimension(R.dimen.item_title_padding_vertical_simple_mode)
            );
        } else {
            viewHolder.infoView.setVisibility(View.VISIBLE);
            viewHolder.titleView.setPadding(
                    getDimension(R.dimen.item_title_padding_horizontal),
                    getDimension(R.dimen.item_title_padding_vertical_sorting_info_mode),
                    getDimension(R.dimen.item_title_padding_horizontal),
                    getDimension(R.dimen.item_title_padding_vertical_sorting_info_mode)
            );
        }

        viewHolder.checkBox.setVisibility(checkBoxVisibility());
    }

    private int checkBoxVisibility() {
        if (isMultipleChoice) return View.VISIBLE;
        else return View.GONE;
    }

    private int radioButtonVisibility() {
        if (isMultipleChoice) return View.GONE;
        else return View.VISIBLE;
    }

    private int getDimension(@DimenRes int dimenResource) {
        return (int) context.getResources().getDimension(dimenResource);
    }

    public void setList(List<FSItem> list) {
        super.clear();
        super.addAll(list);
        notifyDataSetChanged();
    }

    public void updateSelections(@NotNull List<FSItem> list) {
        selectionsList.clear();
        selectionsList.addAll(list);
        notifyDataSetChanged();
    }

    public void changeSortingMode(SortingModeType sortingMode) {
        this.sortingMode = sortingMode;
        notifyDataSetChanged();
    }

    private class ViewHolder {

        final CheckBox checkBox;
        final TextView titleView;
        final TextView infoView;
        final TextView iconView;

        ViewHolder(View view){
            checkBox = view.findViewById(checkBoxId);
            titleView = view.findViewById(titleViewId);
            infoView = view.findViewById(infoViewId);
            iconView = view.findViewById(fileIconViewId);
        }
    }
}
///*
// * Copyright (c) 2016-2017. Vijai Chandra Prasad R.
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see http://www.gnu.org/licenses
// */
//
//package com.walhalla.core.settings;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.os.Environment;
//import android.preference.PreferenceManager;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.content.ContextCompat;
//import androidx.preference.PreferenceDialogFragmentCompat;
//import androidx.recyclerview.widget.DividerItemDecoration;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.walhalla.aaa.Config;
//import com.walhalla.aaa.R;
//import com.walhalla.aaa.activity.MainActivity;
//import com.walhalla.core.folderpicker.DirectoryRecyclerAdapter;
//import com.walhalla.core.folderpicker.Storage;
//import com.walhalla.ui.DLog;
//
//import java.io.File;
//import java.io.FileFilter;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//
//import es.dmoral.toasty.Toasty;
//
///**
// * Created by vijai on 01-12-2016.
// * <p>
// * DialogPreference
// * compat
// */
//
//public class FolderChooserCompat extends PreferenceDialogFragmentCompat implements View.OnClickListener,
//        DirectoryRecyclerAdapter.OnDirectoryClickedListerner {
//
//    private ImageButton up;
//    private ImageButton createDir;
//    private RecyclerView mRecyclerView;
//    private TextView tv_empty;
//    private Spinner spinner;
//    private TextView tv_currentDir;
//
//    private File currentDir = new File("/");
//    private List<File> directories;
//    private android.app.AlertDialog dialog;
//    private DirectoryRecyclerAdapter adapter;
//    private List<Storage> mStorages = new ArrayList<>();
//    private boolean isExternalStorageSelected = false;
//    private SharedPreferences prefs;
//
//    public FolderChooserCompat() {}
//
//
//    public static FolderChooserCompat newInstance(String key) {
//        final FolderChooserCompat fragment = new FolderChooserCompat();
//        final Bundle bundle = new Bundle(1);
//        bundle.putString(ARG_KEY, key);
//        fragment.setArguments(bundle);
//        return fragment;
//    }
//
//
//    /**
//     * @param view
//     */
//
//    @Override
//    protected void onBindDialogView(View view) {
//        super.onBindDialogView(view);
//
//
//        if (!((MainActivity) getActivity()).permissionResolver.resolve()) dismiss();
//
//
//        up = view.findViewById(R.id.nav_up);
//        createDir = view.findViewById(R.id.create_dir);
//        mRecyclerView = 2222222222222222222222222
//        tv_empty = view.findViewById(R.id.empty);
//        spinner = view.findViewById(R.id.storageSpinner);
//        tv_currentDir = view.findViewById(R.id.tv_selected_dir_label);
//
//
////        setPersistent(true);
////        setDialogTitle(null);
////        setDialogLayoutResource(R.layout.dialog_director_chooser);
////        setPositiveButtonText(android.R.string.ok);
////        setNegativeButtonText(android.R.string.cancel);
//        currentDir = new File(
//                getPreference().getSummary().toString()
//        );
//
//
////        setSummary(getPersistedString(currentDir.getPath()));
////        DLog.d( "Persisted String is: " + getPersistedString(currentDir.getPath()));
//
//        //init storage
//        File[] SDCards = ContextCompat.getExternalFilesDirs(getContext().getApplicationContext(), null);
//
//
//        mStorages.add(new Storage(getPreference().getSummary().toString(), Storage.StorageType.CURRENT));
//        mStorages.add(new Storage(11111.getPath(), Storage.StorageType.INTERNAL));
//
//        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
//        if (SDCards.length > 1) {
//            mStorages.add(new Storage(SDCards[1].getPath(), Storage.StorageType.EXTERNAL));
//        }
//        //getRemovableSDPath(SDCards[1]);
//
//
//        directories = generateFoldersList(currentDir);
//        if (!currentDir.canWrite()) {
//            Toasty.error(getContext(), "No permission to write to directory", Toast.LENGTH_SHORT).show();
//        }
//        initView(view);
//
//        //mRecyclerView.setHasFixedSize(true);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
//                RecyclerView.VERTICAL, false);
//        mRecyclerView.setLayoutManager(layoutManager);
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), layoutManager.getOrientation());
//        mRecyclerView.addItemDecoration(dividerItemDecoration);
//
//        adapter = new DirectoryRecyclerAdapter(getContext(), this);
//        mRecyclerView.setAdapter(adapter);
//
////        if (!isDirectoryEmpty()) {
////            adapter.swap(directories);
////        }
//        tv_currentDir.setText(currentDir.getPath());
//    }
//
//    @Override
//    public void onDialogClosed(boolean positiveResult) {
//
//        if (positiveResult) {
//            DLog.d("Directory choosed! " + currentDir.getPath());
//            if (!currentDir.canWrite()) {
//                Toasty.error(getContext(), "Cannot write to selected directory. Path will not be saved.", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            //getPreference().persistString(currentDir.getPath());
//            //onDirectorySelectedListerner.onDirectorySelected();
//            getPreference().setSummary(currentDir.getPath());
//        }
//    }
//
//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
////        if (currentDir == null) return outState;
////        Bundle dialogState = dialog == null ? null : dialog.onSaveInstanceState();
////        outState = new SavedStateHandler(outState, currentDir.getPath(), dialogState);
//        super.onSaveInstanceState(outState);
//    }
//
//
//    @Override
//    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
//        super.onViewStateRestored(savedInstanceState);
//    }
////    @Override
////    protected void onRestoreInstanceState(Parcelable state) {
////        if (state == null || !state.getClass().equals(SavedStateHandler.class)) {
////            super.onRestoreInstanceState(state);
////            return;
////        }
////
////        SavedStateHandler myState = (SavedStateHandler) state;
////        super.onRestoreInstanceState(myState.getoutState());
////
////        setCurrentDir(currentDir.getPath());
////        if (myState.dialogState != null) {
////            // recreate dialog
////            newDirDialog(myState.dialogState);
////        }
////    }
//
//
//    private List<File> generateFoldersList(File currentDir) {
//
//        List<File> files = new ArrayList<>();
//
//        if (currentDir.canRead()) {
//            File[] dir = currentDir.listFiles(new DirectoryFilter());
//            files = new ArrayList<>(Arrays.asList(dir));
//            if (!files.isEmpty()) {
//                Collections.sort(files, new SortFileName());
//            }
//            return files;
//        }
//        return files;
//    }
//
//    private void initView(View view) {
//        up.setOnClickListener(this);
//        createDir.setOnClickListener(this);
//        ArrayList<String> StorageStrings = new ArrayList<>();
//
//        for (Storage storage : mStorages) {
//            Storage.StorageType storageType = storage.getType();
//            if (storageType == Storage.StorageType.INTERNAL) {
//                StorageStrings.add("Internal storage");
//            } else if (storageType == Storage.StorageType.EXTERNAL) {
//                StorageStrings.add("Removable Storage");
//            } else if (storageType == Storage.StorageType.CURRENT) {
//                StorageStrings.add("Current storage");
//            }
//        }
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, StorageStrings);
//
//        // Drop down layout style - list view with radio button
//        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        // attaching data adapter to spinner
//        spinner.setAdapter(dataAdapter);
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position != -1) {
//                    DLog.d("Selected storage is: " + mStorages.get(position));
//                    isExternalStorageSelected = (mStorages.get(position).getType() == Storage.StorageType.EXTERNAL);
//                    if (isExternalStorageSelected && !prefs.getBoolean(Config.ALERT_EXTR_STORAGE_CB_KEY, false)) {
//                        showExtDirAlert();
//                    }
//                    changeDirectory(new File(mStorages.get(position).getPath()));
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//    }
//
//    private void changeDirectory(File file) {
//        currentDir = file;
//        directories = generateFoldersList(currentDir);
//        DLog.d("c Changed dir is: " + file.getPath() + " | " + directories.toString());
//        setCurrentDirLabel(currentDir);
//
//
//        if (directories == null || directories.isEmpty()) {
//            tv_empty.setVisibility(View.VISIBLE);
//            mRecyclerView.setVisibility(View.GONE);
//            adapter.swap(new ArrayList<>());
//        } else {
//            mRecyclerView.setVisibility(View.VISIBLE);
//            tv_empty.setVisibility(View.GONE);
//
//            DLog.d("isDirectoryEmpty: ");
//
//            if (adapter == null) {
//                adapter = new DirectoryRecyclerAdapter(getContext(), this);
//                //00000mRecyclerView.swapAdapter(adapter, true);
//            }
//            adapter.swap(directories);
//        }
//    }
//
//    private void setCurrentDirLabel(File file) {
//
//        if (file != null && file.isDirectory()) {
//            DLog.d("@@@@@@@@@@@@" + file.getPath());
//            if (tv_currentDir != null) {
//                try {
//                    tv_currentDir.setText(file.getPath());
//                } catch (Exception e) {
//                    DLog.handleException(e);
//                }
//            }
//        }
//
//    }
//
//    public void setCurrentDir(String currentDir) {
//        File dir = new File(currentDir);
//        if (dir.exists() && dir.isDirectory()) {
//            this.currentDir = dir;
//            DLog.d("Directory set");
//        } else {
//            createFolder(dir.getPath());
//            DLog.d("Directory created");
//        }
//    }
//
////    public void setOnDirectoryClickedListerner(OnDirectorySelectedListerner onDirectoryClickedListerner) {
////        FolderChooserCompat.onDirectorySelectedListerner = onDirectoryClickedListerner;
////    }
//
//    private void newDirDialog(Bundle savedState) {
//        LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = li.inflate(R.layout.directory_chooser_edit_text, null);
//        final EditText input = view.findViewById(R.id.et_new_folder);
//        input.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (dialog != null) {
//                    Button button = dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
//                    button.setEnabled(!s.toString().trim().isEmpty());
//                }
//            }
//        });
//
//        android.app.AlertDialog.Builder ab = new android.app.AlertDialog.Builder(getContext())
//                .setTitle(R.string.alert_title_create_folder)
//                .setMessage(R.string.alert_message_create_folder)
//                .setView(view)
//                .setNegativeButton(android.R.string.cancel,
//                        (dialog, which) -> dialog.dismiss())
//                .setPositiveButton(android.R.string.ok,
//                        (dialog, which) -> {
//                            dialog.dismiss();
//                            String dirName = input.getText().toString().trim();
//                            if (!dirName.isEmpty()) createFolder(dirName);
//                        });
//
//        dialog = ab.create();
//        if (savedState != null) dialog.onRestoreInstanceState(savedState);
//        dialog.show();
//        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setEnabled(!input.getText().toString().trim().isEmpty());
//    }
//
//    private boolean createFolder(String dirName) {
//        if (currentDir == null) {
//            Toasty.error(getContext(), "No directory selected", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        if (!currentDir.canWrite()) {
//            Toasty.error(getContext(), "No permission to write to directory", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        File newDir;
//        if (dirName.contains(11111.getPath()))
//            newDir = new File(dirName);
//        else
//            newDir = new File(currentDir, dirName);
//        if (newDir.exists()) {
//            Toasty.error(getContext(), "Directory already exists", Toast.LENGTH_SHORT).show();
//            changeDirectory(new File(currentDir, dirName));
//            return false;
//        }
//
//        if (!newDir.mkdir()) {
//            Toasty.error(getContext(), "Error creating directory", Toast.LENGTH_SHORT).show();
//            DLog.d(newDir.getPath());
//            return false;
//        }
//
//        changeDirectory(new File(currentDir, dirName));
//
//        return true;
//    }
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//
//            case R.id.nav_up:
//                File parentDirectory = new File(currentDir.getParent());
//                DLog.d(parentDirectory.getPath());
//                if (!isExternalStorageSelected) {
//                    if (parentDirectory.getPath().contains(mStorages.get(1).getPath()))
//                        changeDirectory(parentDirectory);
//                } else
//                    changeExternalDirectory(parentDirectory);
//                return;
//
//            case R.id.create_dir:
//                newDirDialog(null);
//                return;
//
//            default:
//                break;
//        }
//    }
//
//    private void changeExternalDirectory(File parentDirectory) {
//        String externalBaseDir = getRemovableSDPath(mStorages.get(2).getPath());
//        if (parentDirectory.getPath().contains(externalBaseDir) && parentDirectory.canWrite())
//            changeDirectory(parentDirectory);
//        else if (parentDirectory.getPath().contains(externalBaseDir) && !parentDirectory.canWrite())
//            Toasty.error(getContext(), R.string.external_storage_dir_not_writable, Toast.LENGTH_SHORT).show();
//    }
//
//    private String getRemovableSDPath(String pathSD) {
//        //String pathSD = file.toString();
//        int index = pathSD.indexOf("Android");
//        DLog.d("Short code is: " + pathSD.substring(0, index));
//        String filename = pathSD.substring(0, index - 1);
//        DLog.d("EXTERNAL Base Dir " + filename);
//        return filename;
//    }
//
//    @Override
//    public void OnDirectoryClicked(File file) {
//        if (file.isDirectory()) {
//            changeDirectory(file);
//        }
//    }
//
//    private void showExtDirAlert() {
//        View checkBoxView = View.inflate(getContext(), R.layout.alert_checkbox, null);
//        final CheckBox checkBox = checkBoxView.findViewById(R.id.donot_warn_cb);
//        new android.app.AlertDialog.Builder(getContext())
//                .setTitle(R.string.alert_ext_dir_warning_title)
//                .setMessage(R.string.alert_ext_dir_warning_message)
//                .setView(checkBoxView)
//                .setNeutralButton(android.R.string.ok, (dialogInterface, i) -> {
//                    if (checkBox.isChecked())
//                        prefs.edit().putBoolean(Config.ALERT_EXTR_STORAGE_CB_KEY, true).apply();
//                })
//                .create().show();
//
//    }
//
//
//    private static class DirectoryFilter implements FileFilter {
//        @Override
//        public boolean accept(File file) {
//            return (file.isDirectory()
//                    && !file.isHidden()) || file.getPath().endsWith(".apk");
//        }
//    }
//
//    //sorts based on the files name
//    private static class SortFileName implements Comparator<File> {
//        @Override
//        public int compare(File f1, File f2) {
//            return f1.getName().toLowerCase().compareTo(f2.getName().toLowerCase());
//        }
//    }
//}

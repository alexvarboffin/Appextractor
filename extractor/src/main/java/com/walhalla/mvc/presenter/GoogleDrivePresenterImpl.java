//package com.walhalla.mvc.presenter;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//
//import com.google.android.gms.common.api.CommonStatusCodes;
//import com.google.android.gms.common.api.Scope;
//import com.google.android.gms.common.api.Status;
//import com.google.android.gms.tasks.Task;
//import com.google.api.client.extensions.android.http.AndroidHttp;
//import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
//import com.google.api.client.json.gson.GsonFactory;
//import com.google.api.services.drive.Drive;
//import com.google.api.services.drive.DriveScopes;
//import com.walhalla.ScopeHandler;
//import com.walhalla.aaa.fragment.extractor.ExtractorFragment;
//import com.walhalla.aaa.task.GoogleDriveFileUploadTask;
//import com.walhalla.boilerplate.domain.executor.impl.BackgroundExecutor;
//import com.walhalla.boilerplate.threading.MainThreadImpl;
//import com.walhalla.ui.DLog;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//public class GoogleDrivePresenterImpl implements DrivePresenter {
//
//    private Drive SERVICE;
////    private static Set<Scope> SCOPES = new HashSet<>(7);
////    private static Set<Scope> SCOPES = new HashSet<>(2);
//
//    /**
//     * Request code for auto Google Play Services error resolution.
//     */
//    private static final int REQUEST_CODE_SIGN_IN = 101;
//    private static GoogleDrivePresenterImpl INSTANCE;
//
//    private final ExtractorFragment view;
//
//
//    public List<File> fileList;
//
//
//    //Background task yeah
//    private GoogleDriveFileUploadTask task = null;
//
//
//    private GoogleDrivePresenterImpl(ExtractorFragment view) {
//        this.view = view;
//    }
//
//    public static GoogleDrivePresenterImpl newInstance(ExtractorFragment fragment) {
//        if (INSTANCE == null) {
//            INSTANCE = new GoogleDrivePresenterImpl(fragment);
//        }
//        return INSTANCE;
//    }
//
//    public void signIn(Context context, List<File> file, List<String> appName) {
//        this.fileList = file;
//
////        SCOPES.add(Drive.SCOPE_FILE);
////        SCOPES.add(Drive.SCOPE_APPFOLDER);
//
//
//        //pb_*.jks
//        //https://www.youtube.com/watch?v=p-i-VRCNrMM
//        //https://portal.pfu.gov.ua/sidebar/Templates/HowToReg
//        //chatroulette show
//
//        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(context);
//        if (signInAccount != null && signInAccount.getGrantedScopes().containsAll(ScopeHandler.getScopes())) {
//            initializeDriveClient(signInAccount);
//            DLog.d("SignIn: " + signInAccount.getDisplayName());
//        } else {
//            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(view.getContext(), ScopeHandler.makeOptions());
//            Intent signInIntent = googleSignInClient.getSignInIntent();
//            try {
//                view.startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
//            } catch (Exception e) {
//                DLog.handleException(e);
//            }
//
//            //LogOut
//            //googleSignInClient.signOut();
//        }
//    }
//
////    private GoogleSignInOptions makeOptions() {
////        GoogleSignInOptions.Builder builder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN);
////        //.requestScopes(new Scope("https://www.googleapis.com/auth/drive"))
////
////        //.requestScopes(new Scope(DriveScopes.DRIVE_FILE))
////
//////                                .requestScopes(Drive.SCOPE_FILE)
//////                                .requestScopes(Drive.SCOPE_APPFOLDER)
////
////        //.requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))//SCOPE_APPFOLDER
////        //GoogleSignInOptions signInOptions
////
//////            for (Scope scope : SCOPES) {
//////                DLog.d("INJECT SCOPE --> " + scope);
//////                builder.requestScopes(scope);
//////
////////                builder.requestScopes(new Scope(DriveScopes.DRIVE));
////////                builder.requestScopes(new Scope(DriveScopes.DRIVE_FILE));
////////                builder.requestScopes(new Scope(DriveScopes.DRIVE_READONLY));
////////                builder.requestScopes(new Scope(DriveScopes.DRIVE_APPDATA));
////////                builder.requestScopes(new Scope(DriveScopes.DRIVE_METADATA));
////////                builder.requestScopes(new Scope(DriveScopes.DRIVE_METADATA_READONLY));
////////                builder.requestScopes(new Scope(DriveScopes.DRIVE_PHOTOS_READONLY));
//////            }
////
////       return builder.requestEmail().build();
////    }
//
//    private void initializeDriveClient(GoogleSignInAccount googleAccount) {
//        //mDriveClient = Drive.getDriveClient(getApplicationContext(), account);
//        //mDriveServiceHelper = Drive.getDriveResourceClient(getActivity().getApplicationContext(), account);
//        GoogleAccountCredential credential =
//                GoogleAccountCredential.usingOAuth2(view.getContext(),
////                        Collections.singleton(DriveScopes.DRIVE_FILE)
//                        ScopeHandler.getScopes());
//        credential.setSelectedAccount(googleAccount.getAccount());
//        SERVICE = new Drive.Builder(AndroidHttp.newCompatibleTransport(),
//                new GsonFactory(),
//                credential)
//                .setApplicationName("Privacy Cleaner")
//                .build();
//
//        // The DriveServiceHelper encapsulates all REST API and SAF functionality.
//        // Its instantiation is required before handling any onClick actions.
//        //mDriveServiceHelper = new DriveServiceHelper(drive);
//        onDriveClientReady();
//    }
//
//    private void onDriveClientReady() {
//        //this.repository = new GoogleDriveHelper();
//        task = new GoogleDriveFileUploadTask(
//                BackgroundExecutor.getInstance(),
//                MainThreadImpl.getInstance(),
//                SERVICE, fileList
//        );
//        task.doInBackground(view.getActivity(),
//                new GoogleDriveFileUploadTask.Callback() {
//                    @Override
//                    public void googleDriveFileSuccess(List<com.google.api.services.drive.model.File> files) {
//                        if (view != null) {
//                            view.googleDriveFileSuccess(files);
//                        }
//                    }
//                }
//        );
////        try {
////            task.get(90000, TimeUnit.MILLISECONDS);
////            task.execute(SERVICE);
////        } catch (ExecutionException e) {
////            DLog.handleException(e);
////        } catch (InterruptedException e) {
////            DLog.handleException(e);
////        } catch (TimeoutException e) {
////            DLog.handleException(e);
////        }
//
//    }
//
//    //==============================================================================================
////    private class FSCreator {
////
////        private String target;
////        private DriveFolder driveFolder;
////        private FSCreator mCreator;
////
////
////        void setTarget(String target, DriveFolder parent) {
////            this.target = target;
////            this.driveFolder = parent;
////            mCreator = this;
////        }
////
////        void run() {
////            Log.d(Config.TAG, "[1] search folder - " + target + "-" + driveFolder.getDriveId());
////
////            Query query = new DownloadManager.Query.Builder()
////                    .addFilter(Filters.eq(SearchableField.TITLE, target))
////                    .addFilter(Filters.eq(SearchableField.TRASHED, false))
////                    //search folder
////                    .addFilter(Filters.eq(SearchableField.MIME_TYPE, MIME_TYPE_FOLDER))
////                    .build();
////
////            Task<MetadataBuffer> task2 = getDriveResourceClient().queryChildren(driveFolder, query)
////                    .addOnSuccessListener(getActivity(),
////                            metadataBuffer -> {
////                                if (metadataBuffer.getCount() > 0) {
////                                    for (Metadata metadata : metadataBuffer) {
////
////                                        if (metadata.isFolder()
////                                            //&& !metadata.isTrashed()
////                                        ) {
////                                            ////metadata);
////                                            //Log.d(Config.TAG, childFolder.getDriveId().encodeToString());
////
////                                            driveFolder = metadata.getDriveId().asDriveFolder();
////
////
////                                            if (metadata.isTrashed()) {
////                                                getDriveResourceClient().untrash(driveFolder);
////                                                Log.d(Config.TAG, "Restored");
////                                            }
////
////                                            metadataBuffer.release();
////
////                                            Log.d(Config.TAG, "[2] folder exist - " + target);
////                                            //driveFolder);
////
////                                            if (target.equals(Config.CLOUD_BACKUP_LOCATION)) {
////                                                mpm.googleDriveFolderId(driveFolder.getDriveId().encodeToString());
////                                                mCreator.setTarget(CHILD_FOLDER_NAME, driveFolder);
////                                                mCreator.run();
////                                            } else if (target.equals(CHILD_FOLDER_NAME)) {
////                                                //=================================
////                                                //save file
////                                                mCreator.saveFile();
////                                                //=================================
////                                            }
////                                            break;
////                                        }
////                                    }
////
////                                    //
////                                    //Log.d(Config.TAG, "[3] Folder maybe trashed! " + target + " " + buffer.getCount());
////                                    //createFolder(target);
////                                } else {
////                                    Log.d(Config.TAG, "[3] Folder not created! " + target + " " + metadataBuffer.getCount());
////                                    createFolder(target);
////                                }
////                            }
////                    )
////                    .addOnFailureListener(getActivity(), e -> {
////                        DLog.d( "run: " + e.toString());
////                        showMessage(new LogViewModel(R.drawable.ic_log_ex, "Problem while retrieving results"));
////                    });
////        }
////
////
////        private void createFolder(String title) {
////
////            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
////                    .setTitle(title).build();
////
////            final Task<DriveFolder> folderTask = getDriveResourceClient().createFolder(driveFolder, changeSet)
////                    .addOnSuccessListener(getActivity(),
////                            driveFolder -> {
////                                Log.d(Config.TAG, ":: Created a folder: " + target
////                                        + " " + driveFolder.getDriveId());
////
////                                //DriveFolder obj = result.getDriveFolder();
////                                ////obj);
////                                //
////
////                                //
////
////                                //Drive.DriveApi.fetchDriveId(getGoogleSignInClient(),
////                                //        result.getDriveFolder().getDriveId().encodeToString())
////                                //
////                                //
////                                //.setResultCallback(idCallback);
////
////                                //DriveId:CAESABjOGSD-j9D0rVYoAQ==
////                                //DriveId:CAESABjuGSD-j9D0rVYoAQ==
////
////                                if (target.equals(Config.CLOUD_BACKUP_LOCATION)) {
////                                    //save root folder id
////                                    mpm.googleDriveFolderId(driveFolder
////                                            .getDriveId()
////                                            .encodeToString());
////
////                                    mCreator.setTarget(CHILD_FOLDER_NAME, driveFolder);
////                                    mCreator.run();
////
////                                } else if (target.equals(CHILD_FOLDER_NAME)) {
////                                    //=================================
////                                    this.driveFolder = driveFolder;//<---
////                                    mCreator.saveFile();
////                                    //=================================
////                                }
////                            })
////                    .addOnFailureListener(getActivity(), e -> {
////                        Log.d(Config.TAG, ":: Error while trying to create the folder");
////                    });
////
////        }
////
////
////        private void saveFile() {
////            if (getActivity() != null) {
////                //save file
////                Log.d(Config.TAG, "Save file in folder: " + target);
////                final Task<DriveContents> createContentsTask = getDriveResourceClient().createContents();
////
////                createContentsTask
////                        .continueWithTask(task -> {
////                            Log.d(Config.TAG, fileList);
////
////                            final DriveContents driveContents = task.getResult();
////                            //save file
////                            // Otherwise, we can write our data to the new contents.
////                            // Get an output stream for the contents.
////                            File f = new File(fileList);
////           /*             try {
////                            OutputStream outputStream = driveContents.getOutputStream();
////                            DataOutputStream writer = new DataOutputStream(outputStream);
////
////                            writer.write(readFile(f));
////
////                            try {
////                                writer.close();
////                            } catch (IOException e) {
////                                // TODO Auto-generated catch block
////                                DLog.handleException(e);
////                            }
////
////                        } catch (FileNotFoundException e) {
////                            Log.e(e.getMessage());
////                        } catch (IOException e) {
////                            Log.e(e.getMessage());
////                        }*/
////                            //Log.d(Config.TAG,  "# New contents created." + path + getMimeType(path));
////                            //==========================================================================
////                            // Create the initial metadata - MIME type and title.
////                            // Note that the user will be able to change the title later.
////                            MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
////                                    //.setMimeType("image/jpeg")
////                                    //.setMimeType("audio/aac")
////                                    .setMimeType(getMimeType(fileList))
////                                    .setTitle(f.getName())
////                                    .setStarred(true)//?
////                                    .build();
////
////                            return getDriveResourceClient().createFile(driveFolder, metadataChangeSet, driveContents)
////                                    .addOnSuccessListener(getActivity(), driveFile -> {
////                                        DLog.d( "#####>" + driveFile.getDriveId().encodeToString());
////                                        showMessage(new LogViewModel(R.drawable.ic_log_gd, getString(R.string.gd_success)));
////                                    })
////                                    .addOnFailureListener(getActivity(), e -> {
////                                        showMessage(new LogViewModel(R.drawable.ic_log_ex, "Error while trying to create new file contents"
////                                                + e.getLocalizedMessage()));
////                                    });
////                        });
////
////
//////                        .addOnSuccessListener(getActivity(),
//////                                driveFile -> {
//////                                    DLog.d( "====>" + driveFile.getDriveId().encodeToString());
//////                                    showMessage(
//////                                            new LogViewModel(R.drawable.ic_log_gd,
//////                                                    getString(R.string.file_created, driveFile.getDriveId()
//////                                                            .encodeToString())));
//////                                })
//////                        .addOnFailureListener(getActivity(), e -> {
//////                            showMessage(
//////                                    new LogViewModel(R.drawable.ic_log_ex,
//////                                            "Error while trying to create new file contents"));
//////                        });
////            }
////        }
////
////    }
//
//
//    ///Config.CLOUD_BACKUP_LOCATION
//
//
//    /**
//     * application/vnd.android
//     * application/vnd.google-apps.spreadsheet
//     * "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
//     *
//     * @return
//     */
//
////    @Override
//    public void onActivityResult(int requestCode, int resultCode, @NonNull Intent result) {
//        DLog.d("@@@ result: " + result.toString());
//
//        switch (requestCode) {
//            case REQUEST_CODE_SIGN_IN:
//                if (resultCode != Activity.RESULT_OK) {
//                    // Sign-in may fail or be cancelled by the user. For this sample, sign-in is
//                    // required and is fatal. For apps where sign-in is optional, handle
//                    // appropriately
//
//                    Bundle bundle = result.getExtras();
//                    if (bundle != null) {
//                        //com.google.android.gms.common.api.Status;
//                        Status status = bundle.getParcelable("googleSignInStatus");
//
//                        if (status != null && status.getStatusCode() == CommonStatusCodes.CANCELED) {
//                            //<--------------------
//                            DLog.d("onActivityResult: " + status.getResolution());
//                        } else {
//                            //{statusCode=unknown status code: 12500, resolution=null}
//                            //notfound user account
//                        }
//
//                        for (String key : bundle.keySet()) {
//                            Object value = bundle.get(key);
//                            if (value != null) {
//                                DLog.d(String.format("%s %s (%s)", key,
//                                        value.toString(), value.getClass().getName()));
//                            }
//                        }
//                    }
//                    return;
//                }
//
//                GoogleSignIn.getSignedInAccountFromIntent(result)
//                        .addOnSuccessListener(googleAccount -> {
//                            DLog.d("Signed in as " + googleAccount.toString());
//                            initializeDriveClient(googleAccount);
//                        })
//                        .addOnFailureListener(DLog::handleException);
//
////                if (getAccountTask.isSuccessful()) {
////                    initializeDriveClient(getAccountTask.getResult());
////                } else {
////                    Log.e(TAG, "Sign-in failed.");
////
////                }
//                break;
//
////            case REQUEST_CODE_OPEN_ITEM:
////                if (resultCode == RESULT_OK) {
////                    DriveId driveId = result.getParcelableExtra(
////                            OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID);
////                    mOpenItemTaskSource.setResult(driveId);
////                } else {
////                    mOpenItemTaskSource.setException(new RuntimeException("Unable to open file"));
////                }
////                break;
//        }
//    }
//
//
//}

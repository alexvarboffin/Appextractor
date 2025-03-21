//package com.walhalla.aaa.task;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.text.Spannable;
//import android.text.SpannableStringBuilder;
//
//
//import com.google.api.services.drive.Drive;
//import com.google.api.services.drive.model.File;
//
//import com.walhalla.aaa.R;
//import com.walhalla.aaa.Util;
//import com.walhalla.boilerplate.domain.executor.Executor;
//import com.walhalla.boilerplate.domain.executor.MainThread;
//import com.walhalla.boilerplate.domain.interactors.base.AbstractInteractor;
//import com.walhalla.mvc.repository.GoogleDriveHelper;
//import com.walhalla.ui.DLog;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.walhalla.aaa.Config.CLOUD_BACKUP_LOCATION_GOOGLE_DRIVE;
//
//111 public class GoogleDriveFileUploadTask extends AbstractInteractor {
//
//    private ProgressDialog pd;
//    private java.io.File currentFile;
//
//    private final GoogleDriveHelper helper;
//    private final List<java.io.File> files;
//    private String fileSize;
//
//    public GoogleDriveFileUploadTask(Executor threadExecutor, MainThread mainThread,
//                                     Drive drive,
//                                     List<java.io.File> fileName) {
//        super(threadExecutor, mainThread);
//        this.helper = GoogleDriveHelper.getInstance(drive);
//        this.files = fileName;
//    }
//
//    /**
//     * Google Drive
//     */
//    private final MediaHttpUploaderProgressListener progressListener = mediaHttpUploader -> {
//        if (mediaHttpUploader == null) return;
//        switch (mediaHttpUploader.getUploadState()) {
//            case INITIATION_STARTED:
//                if (DLog.nonNull(pd)) {
//                    mMainThread.post(() -> {
//                        pd.setTitle("Wait! Initiation has started!");
//                    });
//                }
//                break;
//            case INITIATION_COMPLETE:
//                if (DLog.nonNull(pd)) {
//                    mMainThread.post(() -> {
//                        pd.setTitle("Wait! Initiation is complete!");
//                    });
//                }
//                break;
//            case MEDIA_IN_PROGRESS:
//                double percent = mediaHttpUploader.getProgress() * 100;
//                //555              notif.setProgress(percent, files).fire();
//                onProgressUpdate((int) percent);
//                DLog.d("progress -> " + percent);
//                break;
//
//            case MEDIA_COMPLETE:
//                if (DLog.nonNull(pd)) {
//                    mMainThread.post(() -> {
//                        if (pd != null) {
//                            pd.setTitle("Upload is complete!");
//                        }
//                    });
//                }
//        }
//    };
//
//
//    @Override
//    public void run() {
//
//    }
//
//    private void onProgressUpdate(int percent) {
//        mMainThread.post(() -> {
//            SpannableStringBuilder message = new SpannableStringBuilder("Upload to GoogleDrive:\n");
//            SpannableStringBuilder sb = new SpannableStringBuilder(currentFile.getAbsolutePath());
//            sb.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
//                    0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            message.append(sb);
//            message.append("\t").append(fileSize).append("\n").append(String.valueOf(percent)).append("%");
//
//            this.pd.setProgress(percent);
//            this.pd.setMessage(message);
//        });
//    }
//
//    public void doInBackground(Context a, Callback callback) {
//
//        mMainThread.post(() -> {
//
//            pd = Util.loadDialog(a, R.drawable.ic_google_drive);
//
//// Add ok button listener code.
////        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok",
////                new DialogInterface.OnClickListener() {
////
////                    @Override
////                    public void onClick(DialogInterface dialog, int which) {
////                        // Get current onProgress value.
//////                        int currProgress = progressDialog.getProgress();
//////
//////                        // Calculate and format onProgress percentage.
//////                        DecimalFormat df = new DecimalFormat("##.#%");
//////                        float percentage = (float)currProgress / (float)progressDialog.getMax();
//////                        String percentageStr = df.format(percentage);
////
////                        // Show percentage.
////                        //progressDialogValueTextView.setText("Current percentage is " + percentageStr);
////                    }
////                }
////
////        );
//
//            // Add cancel button listener code.
//            pd.setButton(DialogInterface.BUTTON_NEGATIVE, a.getString(android.R.string.cancel),
//                    new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            //progressDialogValueTextView.setText("You canceled the onProgress.");
//                            //UploadFileTask.this.cancel(true)
//                            mThreadExecutor.terminate();
//                        }
//                    }
//            );
//            pd.show();
//
//            //progressDialog.getWindow().setGravity(Gravity.BOTTOM);
//            //this.weakReference = new WeakReference<>(a);
//        });
//
//
//        mThreadExecutor.submit(() -> {
//            List<File> RESULT = new ArrayList<>();
//            String folderId = "folderID";
//            for (java.io.File file : files) {
//
//                currentFile = file;
//                fileSize = Util.getFileSizeMegaBytes(file);
//
//                //Create folder
//                try {
//                    //new FileUploadProgressListener(file.getName())
//                    // new java.io.File(helper.fileList)
//                    /**
//                     * Find root folder what we need
//                     */
//                    File tmp = helper.getUploadDirFile(CLOUD_BACKUP_LOCATION_GOOGLE_DRIVE, null);
//                    final File file1 = helper.saveFileToDriveFolder(tmp, file, progressListener);
//                    RESULT.add(file1);
//
////                Drive SERVICE = drive[0];
////                String path = "APK_EXTRACTED-123";
////                FileList result = SERVICE.files()
////                        .list()
////                        //.setCorpus("domain")
////                        .setSpaces("drive")
////                        .setQ("name='" + path + "' and mimeType='application/vnd.google-apps.folder'")//"'" + path + "' in parents" `name` `=` 'My document'
////                        .setPageSize(1)
////                        .execute();
////
////                //Log.d(TAG, "@@@@@@@@@: " + result.toPrettyString());
////
////                for (File file : result.getFiles()) {
////                    Log.d(TAG, "Found file: " + file.getName() + " " + file.getId());
////                }
//
////                FileList fileList = SERVICE.files()
////                        .list()
////                        .setPageSize(1)
////                        .execute();
////
////                Log.d(TAG, "doInBackground: " + fileList.toPrettyString());
////
////                List<File> files = fileList.getFiles();
////                for (File file : files) {
////                    Log.d(TAG, "###: " + files.size() + "\t"
////                            + file.getParents() + "\t" + file.getName() + "\t" + file.getId());
////                }
//
////                File result = drive[0].files().get("1cZWjKbHYrkXE8NfUin0W8niJISWzqcML")
////                        //.setFields("parents")
////                        .execute();
////                result.getParents();
////                if (result.getParents() != null) {
////                    List<Pair<String, String>> pairs = new ArrayList<>();
////                    for (String parent : result.getParents()) {
//////                        if (folders.containsKey(parent)) {
//////                            pairs.add(new Pair<>(parent, folders.get(parent)));
//////                        } else /*if (item.getBoolean("isRoot")) */ {
//////                            pairs.add(new Pair<>("root", "root"));
//////                        }
////                        Log.d(TAG, "123: " + parent);
////                    }
////
////                }
////
////
////                File fileMetadata = new File();
////                fileMetadata.setName(folderId);
////                fileMetadata.setMimeType("application/vnd.google-apps.folder");
////                List<String> parents = new ArrayList<>();
////                parents.add("/");
////                fileMetadata.setParents(parents);
////                File file = drive[0].files().create(fileMetadata)
////                        .execute();
//                } catch (Exception e) {
//                    DLog.handleException(e);
//                }
//
////            File fileMetadata = new File();
////            fileMetadata.setName("Invoices");
////            fileMetadata.setCreatedTime(new DateTime("2011-05-03T11:58:01Z"));
////            fileMetadata.setParents(Collections.singletonList(folderId));
////            fileMetadata.setMimeType("application/vnd.google-apps.folder");
////            try {
////
////
////                File file = drive[0].files()
////                        .create(fileMetadata)
////                        .setFields("id, parent")
////                        .execute();
////
////                Log.d(TAG, "Folder id: " + file.getId());
////
////
////                // Print the names and IDs for up to 10 files.
////                FileList result = drive[0].files().list()
////                        .setPageSize(10)
////                        .setFields("nextPageToken, files(id, name)")
////                        .execute();
////                List<File> files = result.getFiles();
////                if (files == null || files.isEmpty()) {
////                    System.out.makeLog("No files found.");
////                } else {
////                    System.out.makeLog("Files:");
////                    for (File file1 : files) {
////                        Log.d(TAG, file1.getName() + "\t" + file1.getId());
////                    }
////                }
////
////
////            } catch (IOException e) {
////                Log.d(TAG, "Exception: ->" + e.getLocalizedMessage());
////            }
//            }
//            onPostExecute(RESULT, callback);
//        });
//
//    }
//
//    private void onPostExecute(List<File> file, Callback callback) {
//        if (callback != null) {
//            mMainThread.post(() -> callback.googleDriveFileSuccess(file));
//        }
//        pd.dismiss();
//        pd = null;
//    }
//
//    //extends MediaHttpUploaderProgressListener
//    public interface Callback {
//        void googleDriveFileSuccess(List<File> files);
//    }
//}

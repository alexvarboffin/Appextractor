//package com.walhalla.apkcloudextractor.fragment;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.PowerManager;
//import android.provider.Settings;
//import android.util.Log;
//import android.view.View;
//import android.webkit.MimeTypeMap;
//import android.widget.Button;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.api.client.extensions.android.http.AndroidHttp;
//import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
//import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
//import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
//import com.google.api.client.http.HttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.jackson2.JacksonFactory;
//import com.google.api.client.util.ExponentialBackOff;
//import com.google.api.services.drive.Drive;
//import com.google.api.services.drive.DriveScopes;
//import com.google.api.services.drive.model.File;
//import com.google.api.services.drive.model.FileList;
//import com.walhalla.apkcloudextractor.BuildConfig;
//import com.walhalla.apkcloudextractor.R;
//
//
//import java.io.IOException;
//import java.util.Arrays;
//
//import static android.widget.Toast.*;
//
//public class MainActivity extends AppCompatActivity {
//    private static final String TAG = "CloudBackupActivity";
//    boolean isRestoreBackup;
//    GoogleAccountCredential mCredential;
//    Drive mService;
//    private static final String[] SCOPES = {DriveScopes.DRIVE_METADATA_READONLY, DriveScopes.DRIVE};
//    private ProgressDialog mProgress;
//    String accessToken;
//    boolean isUpload, isDownload;
//    PowerManager.WakeLock mWakeLock;
//    String device;
//    public static final String APP_DIR_NAME = ".SafeFolderAndVault";
//    public static final String PREF_ACCOUNT_NAME = "accountName";
//    public static final int REQUEST_ACCOUNT_PICKER = 1000;
//    public static final int REQUEST_AUTHORIZATION = 1001;
//    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
//    public static final String DRIVE_DIRECTORY_NAME = "SafeFolderAndVault_" + Build.DEVICE;
//    Button btn_upload_drive;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_main);
//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "My Tag");
//        device = Settings.Secure.getString(getContentResolver(),
//                Settings.Secure.ANDROID_ID);
//        mCredential = GoogleAccountCredential.usingOAuth2(
//                getApplicationContext(), Arrays.asList(SCOPES))
//                .setBackOff(new ExponentialBackOff());
//
//        btn_upload_drive = (Button) findViewById(R.id.about_version);
//        btn_upload_drive.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (BuildConfig.DEBUG) Log.i(TAG, "onClick: ");
//                getDataFromAPI(false);
//            }
//        });
//
//    }
//
//    private class UploadBackupTask extends AsyncTask<Void, Void, Void> {
//        private Exception mLastError = null;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            if (!MainActivity.this.isFinishing()) {
////show dialog
//                mProgress = new ProgressDialog(MainActivity.this);
//                mProgress.setMessage("Uploading Backup to Drive...");
//                mProgress.setIndeterminate(false);
//                mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                mProgress.setCancelable(false);
//                mProgress.show();
//            }
//
//        }
//
//        @Override
//        protected File doInBackground(Void... params) {
//            try {
//                if (BuildConfig.DEBUG) Log.i(TAG, "doInBackground: ");
//                mService = getDriveService(mCredential);
//                java.io.File f = new java.io.File(11111 + java.io.File.separator + APP_DIR_NAME);
//                if (f != null)
//                    uploadFiles(f, getUploadDirFile(DRIVE_DIRECTORY_NAME, null));
//                return null;
//            } catch (Exception e) {
//                mLastError = e;
//                DLog.handleException(e);
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(File output) {
//            if (MainActivity.this != null)
//                mProgress.dismiss();
//            if (mWakeLock.isHeld()) mWakeLock.release();
//            ---akeText(MainActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        protected void onCancelled() {
//            if (MainActivity.this != null)
//                mProgress.dismiss();
//            if (mWakeLock.isHeld()) mWakeLock.release();
//            if (mLastError != null) {
//                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
//                    showGooglePlayServicesAvailabilityErrorDialog(
//                            ((GooglePlayServicesAvailabilityIOException) mLastError)
//                                    .getConnectionStatusCode());
//                } else if (mLastError instanceof UserRecoverableAuthIOException) {
//                    startActivityForResult(
//                            ((UserRecoverableAuthIOException) mLastError).getIntent().setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION),
//                            REQUEST_AUTHORIZATION);
//                } else {
//                    ---akeText(MainActivity.this, "The following error occurred:\n"
//                            + mLastError.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            } else {
//                ---akeText(MainActivity.this, "Request cancelled.", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//
//    //
//    public Drive getDriveService(GoogleAccountCredential credential) {
//        if (BuildConfig.DEBUG) Log.i(TAG, "getDriveService: ");
//        try {
//            HttpTransport transport = AndroidHttp.newCompatibleTransport();
//            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
//            mService = new Drive.Builder(transport, jsonFactory, credential)
//                    .setApplicationName(getResources().getString(R.string.app_name)).
//                            build();
//            Drive.Files.List request = mService.files().list();
//            FileList files = request.execute();
//            if (BuildConfig.DEBUG) Log.i(TAG, "getDriveService: filesList-->" + files.getFiles());
//            return mService;
//        } catch (IOException ex) {
//            if (BuildConfig.DEBUG) Log.i(TAG, "getDriveService: catch" + ex);
//            ex.printStackTrace();
//            if (ex instanceof UserRecoverableAuthIOException)
//                startActivityForResult(((UserRecoverableAuthIOException) ex)
//                        .getIntent().setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION), REQUEST_AUTHORIZATION);
//        }
//        return null;
//    }
//
//    private void uploadFiles(java.io.File f, File file) {
//        try {
//            java.io.File[] path = f.listFiles();
//            if (path != null && path.length > 0) {
//                if (BuildConfig.DEBUG) Log.i(TAG, "path-->" + path.length);
//                for (int i = 0; i < path.length; i++) {
//                    if (path[i].isDirectory()) {
//                        if (BuildConfig.DEBUG)
//                            Log.i(TAG, "createFiles: else directory->" + path[i].getName());
//                        uploadFiles(path[i], getUploadDirFile(path[i].getName(), file));
//                    } else {
//                        uploadFiles(path[i], getUploadFiles(path[i], path[i].getName(), file));
//                    }
//                }
//            }
//        } catch (Exception e) {
//            DLog.handleException(e);
//        }
//    }
//
//    public String getMimiTypeFromFile(java.io.File selected) {
//        Uri selectedUri = Uri.fromFile(selected);
//        String fileExtension
//                = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
//        String mimeType
//                = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
//        if (BuildConfig.DEBUG) Log.d("TAG", "MimeType=" + mimeType);
//        return mimeType;
//    }
//
//    private File getUploadFiles(java.io.File filesName, String fileName, File parentFileName) {
//        try {
//            Drive.Children.List request = mService.children().list(parentFileName.getId());
//            ChildList children = request.execute();
//
//            File fileMetadata = new File();
//
//            FileContent mediaContent = null;
//            for (ChildReference child : children.getItems()) {
//                File childfile;
//                if (child.size() > 0) {
//                    if (BuildConfig.DEBUG) Log.i(TAG, "getFiles: file size-->" + child.size());
//                    childfile = mService.files().get(child.getId()).execute();
//                    if (childfile != null && fileName.equals(childfile.getTitle())) {
//                        FileContent mediaContent1 = new FileContent(childfile.getMimeType(), filesName);
//                        File updatedFile = mService.files().update(child.getId(), childfile, mediaContent1).execute();
//                        if (BuildConfig.DEBUG)
//                            Log.i(TAG, "getFiles: file->" + updatedFile.getId() + " name--" + updatedFile.getTitle());
//                        return updatedFile;
//                    }
//                } else {
//                    if (parentFileName != null) {
//                        fileMetadata.setParents(Collections.singletonList(new ParentReference().setId(child.getId())));
//                        mediaContent = new FileContent(getMimiTypeFromFile(filesName), filesName);
//                        childfile = mService.files().create(fileMetadata, mediaContent)
//                                .setFields("id, parents")
//                                .execute();
//                    } else {
//                        childfile = mService.files().create(fileMetadata, mediaContent)
//                                .setFields("id")
//                                .execute();
//                    }
//                    return childfile;
//                }
//            }
//        } catch (Exception e) {
//            DLog.handleException(e);
//        }
//
//        try {
//            File childfile;
//            FileContent mediaContent = null;
//            File fileMetadata = new File();
//            fileMetadata.setTitle(fileName);
//            String fileId = parentFileName.getId();
//            if (parentFileName != null) {
//                fileMetadata.setParents(Collections.singletonList(new ParentReference().setId(fileId)));
//                mediaContent = new FileContent(getMimiTypeFromFile(filesName), filesName);
//                childfile = mService.files().create(fileMetadata, mediaContent)
//                        .setFields("id, parents")
//                        .execute();
//            } else {
//                childfile = mService.files().create(fileMetadata, mediaContent)
//                        .setFields("id")
//                        .execute();
//            }
//            return childfile;
//        } catch (IOException e) {
//            DLog.handleException(e);
//        }
//        return null;
//    }
//
//

//
//    @Override
//    protected void onActivityResult(
//            int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case REQUEST_GOOGLE_PLAY_SERVICES:
//                if (resultCode != RESULT_OK) {
//                    ---akeText(MainActivity.this, "This app requires Google Play Services. Please install " +
//                            "Google Play Services on your device and relaunch this app.", Toast.LENGTH_SHORT).show();
//                } else {
//                    getDataFromAPI(isRestoreBackup);
//                }
//                break;
//            case REQUEST_ACCOUNT_PICKER:
//                if (resultCode == RESULT_OK && data != null &&
//                        data.getExtras() != null) {
//                    String accountName =
//                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
//                    if (accountName != null) {
//                        SharedPreferences settings =
//                                getPreferences(Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = settings.edit();
//                        editor.putString(PREF_ACCOUNT_NAME, accountName);
//                        editor.apply();
//                        mCredential.setSelectedAccountName(accountName);
//                        getDataFromAPI(isRestoreBackup);
//                    }
//                }
//                break;
//            case REQUEST_AUTHORIZATION:
//                if (resultCode == RESULT_OK) {
//                    getDataFromAPI(isRestoreBackup);
//                }
//                break;
//        }
//    }
//
//    private void chooseAccount() {
//        String accountName = getPreferences(Context.MODE_PRIVATE)
//                .getString(PREF_ACCOUNT_NAME, null);
//        if (accountName != null) {
//            mCredential.setSelectedAccountName(accountName);
//            getDataFromAPI(isRestoreBackup);
//        } else {
//// Start a dialog from which the user can choose an account
//            startActivityForResult(
//                    mCredential.newChooseAccountIntent().setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION),
//                    REQUEST_ACCOUNT_PICKER);
//        }
//    }
//
//    private void getDataFromAPI(boolean isRestoreBackup) {
//        if (!isGooglePlayServicesAvailable()) {
//            acquireGooglePlayServices();
//        } else if (mCredential.getSelectedAccountName() == null) {
//            chooseAccount();
//        } else if (!isNetworkAvailable(MainActivity.this)) {
//            ---akeText(MainActivity.this, "No network connection available.", Toast.LENGTH_SHORT).show();
//        } else {
//            if (BuildConfig.DEBUG)
//                Log.i(TAG, "getResultsFromApi:!isRestoreBackup " + isRestoreBackup);
//            if (isNetworkAvailable(MainActivity.this))
//                new UploadBackupTask().execute();
//        }
//    }
//
//    private boolean isGooglePlayServicesAvailable() {
//        GoogleApiAvailability apiAvailability =
//                GoogleApiAvailability.getInstance();
//        final int connectionStatusCode =
//                apiAvailability.isGooglePlayServicesAvailable(this);
//        return connectionStatusCode == ConnectionResult.SUCCESS;
//    }
//
//    private void acquireGooglePlayServices() {
//        GoogleApiAvailability apiAvailability =
//                GoogleApiAvailability.getInstance();
//        final int connectionStatusCode =
//                apiAvailability.isGooglePlayServicesAvailable(this);
//        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
//            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
//        }
//    }
//
//    void showGooglePlayServicesAvailabilityErrorDialog(
//            final int connectionStatusCode) {
//        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
//        Dialog dialog = apiAvailability.getErrorDialog(
//                MainActivity.this,
//                connectionStatusCode,
//                REQUEST_GOOGLE_PLAY_SERVICES);
//        dialog.show();
//    }
//
//    public static boolean isNetworkAvailable(Context context) {
//        ConnectivityManager connectivityManager
//                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
//    }
//}
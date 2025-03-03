package com.walhalla.appextractor.task;

import android.content.Context;
import android.os.AsyncTask;

import com.dropbox.core.DbxException;
import com.dropbox.core.util.IOUtil;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.DbxPathV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.SearchMatch;
import com.dropbox.core.v2.files.SearchMode;
import com.dropbox.core.v2.files.SearchResult;
import com.dropbox.core.v2.files.WriteMode;
import com.walhalla.appextractor.Config;
import com.walhalla.appextractor.Troubleshooting;
import com.walhalla.appextractor.Util;
import com.walhalla.db.DropboxClientFactory;
import com.walhalla.ui.DLog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Async task to upload a file to a directory
 */
public class UploadFileTask extends AsyncTask<List<String>, Integer, List<FileMetadata>> {

    private final Context mContext;
    private final DbxClientV2 mDbxClient;
    private final Callback mCallback;
    private Exception mException;

    //Current file
    private File localFile;
    private String fileSize;


    public interface Callback {
        void onUploadComplete(List<FileMetadata> result);

        void onError(Exception e);

        void progressUpdate(Integer[] progress, File localFile, String fileSize);
    }

    public UploadFileTask(Context context, DbxClientV2 dbxClient, Callback callback) {
        mContext = context;
        mDbxClient = dbxClient;
        mCallback = callback;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        if (mCallback != null) {
            mCallback.progressUpdate(progress, localFile, fileSize);
        }
    }


    @Override
    protected void onPostExecute(List<FileMetadata> result) {
        super.onPostExecute(result);
        if (mException != null) {
            mCallback.onError(mException);
        } else if (result == null) {
            mCallback.onError(null);
        } else {
            mCallback.onUploadComplete(result);
        }
    }


    @Override
    protected List<FileMetadata> doInBackground(List<String>... params) {
        List<FileMetadata> data = new ArrayList<>();
        List<String> files = params[0];
        int total = files.size();

        for (int i = 0; i < files.size(); i++) {

            this.localFile = new File(files.get(i));//@@@ UriHelpers.getFileForUri(mContext, Uri.parse(files));
            this.fileSize = Util.getFileSizeMegaBytes(localFile);

            if (localFile != null) {
                String remoteFolderPath = "/" + Config.__CLOUD_BACKUP_LOCATION_DROP_BOX;//params[1];
                String pathError = DbxPathV2.findError(remoteFolderPath);
                if (pathError != null) {
                    DLog.d("Invalid <dropbox-path>: " + pathError);
                }

                // Note - this is not ensuring the name is a valid dropbox file name
                String remoteFileName = localFile.getName();

                try {
                    InputStream in = Troubleshooting.streamLoader(mContext, localFile);
                    IOUtil.ProgressListener progressListener = l -> printProgress(l, total);
                    FileMetadata aa = mDbxClient.files()
                            .uploadBuilder(remoteFolderPath + "/" + remoteFileName)
                            .withMode(WriteMode.OVERWRITE) //WriteMode.ADD
                            .uploadAndFinish(in, progressListener); //.uploadAndFinish(inputStream);
                    if (aa != null) {
                        data.add(aa);
                    }
                } catch (DbxException | IOException e) {
                    mException = e;
                }
            }
        }
        return data;
    }

    private void printProgress(long uploaded, long size) {
        //DLog.d(String.format("Uploaded %12d / %12d bytes (%5.2f%%)\n", uploaded, size, 100 * (uploaded / (double) size)));
        publishProgress((int) (100 * (uploaded / (double) size)));
    }

//    WE NOT USE URI
//    @Override
//    protected List<FileMetadata> doInBackground(String[]... params) {
//        List<FileMetadata> data = new ArrayList<>();
//
//        String[] localUri = params[0];
//
//        for (int i = 0; i < localUri.length; i++) {
//
//            localFile = UriHelpers.getFileForUri(mContext, Uri.parse(localUri[i]));
//            fileSize = Util.getFileSizeMegaBytes(localFile);
//
//            if (localFile != null) {
//                String remoteFolderPath = "/" + Config.__CLOUD_BACKUP_LOCATION_DROP_BOX;//params[1];
//                String pathError = DbxPathV2.findError(remoteFolderPath);
//                if (pathError != null) {
//                    DLog.d("Invalid <dropbox-path>: " + pathError);
//                }
//
//                // Note - this is not ensuring the name is a valid dropbox file name
//                String remoteFileName = localFile.getName();
//
//                try (InputStream inputStream = new FileInputStream(localFile)) {
//                    FileMetadata aa = mDbxClient.files().uploadBuilder(remoteFolderPath + "/" + remoteFileName)
//                            .withMode(WriteMode.OVERWRITE) //WriteMode.ADD
//                            .uploadAndFinish(inputStream);
//                    if (aa != null) {
//                        data.add(aa);
//                    }
//                } catch (DbxException | IOException e) {
//                    mException = e;
//                }
//            }
//        }
//        return data;
//    }


    public boolean folderExist(String path, String fileName) {
        DbxClientV2 mDbxClient = DropboxClientFactory.getClient();
        try {
            SearchResult searchResult = mDbxClient.files().searchBuilder("", Config.__CLOUD_BACKUP_LOCATION_DROP_BOX)
                    .withMode(SearchMode.FILENAME)
                    .withMaxResults((long) 1)
                    .start();
            List<SearchMatch> searchMatches = searchResult.getMatches();
            return !searchMatches.isEmpty();

        } catch (DbxException e) {
            DLog.handleException(e);
        }
        return false;
    }

}

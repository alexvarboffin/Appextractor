//package com.walhalla.mvc.repository;
//
//import com.google.api.client.googleapis.media.MediaHttpUploader;
//import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
//import com.google.api.client.http.FileContent;
//import com.google.api.client.util.DateTime;
//import com.google.api.services.drive.Drive;
//
//import com.google.api.services.drive.model.File;
//import com.google.api.services.drive.model.FileList;
//import com.walhalla.aaa.BuildConfig;
//import com.walhalla.aaa.Util;
//import com.walhalla.ui.DLog;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//
//public class GoogleDriveHelper implements CloudRepository {
//
//    private static GoogleDriveHelper instance;
//    private final Drive SERVICE;
//    private static final String MIME_TYPE_FOLDER = "application/vnd.google-apps.folder";
//
//
//    public static GoogleDriveHelper getInstance(Drive drive) {
//        if (instance == null) {
//            instance = new GoogleDriveHelper(drive);
//        }
//        return instance;
//    }
//
//    private GoogleDriveHelper(Drive drive) {
//        this.SERVICE = drive;
//    }
//
//    @Override
//    public void signIn() {
//
//    }
//
//    public File saveFileToDriveFolder(File rootFolder, java.io.File file, MediaHttpUploaderProgressListener listener) throws IOException {
//
//
//        final String path = file.getAbsolutePath();
//        String mimeType = Util.getMimeType(path);
//        DLog.d(Util.getMimeType(path));
//        File body = new File();
//        body.setName(file.getName());
//        body.setCreatedTime(new DateTime("2011-05-03T11:58:01Z"));
//        body.setParents(
//                //Collections.singletonList("app_folder")
//                Collections.singletonList(rootFolder.getId())
//        );
//        body.setMimeType(mimeType);
//
//        // For mime type of specific f1 visit Drive Doucumentation
//
//        java.io.File file2 = new java.io.File(path);
////        InputStream inputStream = getResources().openRawResource(R.raw.template);
////        try {
////            FileUtils.copyInputStreamToFile(inputStream, file2);
////        } catch (IOException e) {
////            DLog.handleException(e);
////        }
//
//        FileContent mediaContent = new FileContent(mimeType, file2);
//
//
//        Drive.Files.Create cc = SERVICE.files()
//                .create(body, mediaContent)
//                .setFields("id"); //Response fields
//        MediaHttpUploader mediaHttpUploader = cc.getMediaHttpUploader();
//        mediaHttpUploader.setDirectUploadEnabled(false);
//        mediaHttpUploader.setChunkSize(
//                //MediaHttpUploader.MINIMUM_CHUNK_SIZE * 2
//                //1 * 1024 * 1024 /* bytes */
//                //MediaHttpUploader.DEFAULT_CHUNK_SIZE
//                MediaHttpUploader.MINIMUM_CHUNK_SIZE
//        ); //10*1024*1024 previously I am using 1000000 thats why it won't work
//        mediaHttpUploader.setProgressListener(listener);
//        File f1 = cc.execute();
//
//
//        DLog.d("@@@@@@@@@@@@@@@@@:" + f1.toPrettyString());
//
////        ---akeText(getApplicationContext(),
////                "File created:"+f1.getId() , Toast.LENGTH_SHORT).show();
//        DLog.d("#####>" + f1.getName());
//        return f1;
//    }
//
//    public boolean createFolder(String path, String folderName/*, Account account*/) {
//        try {
//            File fileMetadata = new File();
//            fileMetadata.setName(folderName);
//            fileMetadata.setMimeType(MIME_TYPE_FOLDER);
//            fileMetadata.setFolderColorRgb("#ff0000");
//            fileMetadata.setId("11111111111111");
//            List<String> parents = new ArrayList<>();
//            parents.add(path);
//            fileMetadata.setParents(parents);
//            File file = SERVICE.files().create(fileMetadata)
//                    .execute();
//        } catch (IOException e) {
//            DLog.handleException(e);
//        }
//        return true;
//
//    }
//
//    //@Override
//    public File findFolderByName(File parentFile) throws IOException {
//        FileList result = SERVICE.files()
//                .list()
//                //.setCorpus("domain")
//                .setSpaces("drive")
//                .setQ("id='" + parentFile.getId() + "' and mimeType='" + MIME_TYPE_FOLDER + "'")//"'" + path + "' in parents" `name` `=` 'My document'
//                .setPageSize(1)
//                .execute();
//
//        List<File> files = result.getFiles();
//        if (files.size() > 0) {
//            return files.get(0);
//        }
//        return null;
//    }
//
//
//    //@Override
//    public File getParentFolder(String dirName) throws IOException {
//        Drive.Files.List request = SERVICE.files().list()
//                .setQ("mimeType='" + MIME_TYPE_FOLDER
//                        + "' and trashed = false"
//                        + " and name = '" + dirName + "'")
//                .setSpaces("drive")
//                .setFields("nextPageToken, files(id, name)");
//
////        Drive.Files.List request = SERVICE.files()
////                .list()
////                //.setCorpus("domain")
////                .setSpaces("drive")
////                .setQ("name ='" + dirName + "' and mimeType='" + MIME_TYPE_FOLDER + "'")//"'" + path + "' in parents" `name` `=` 'My document'
////                .setPageSize(1);
//
//        DLog.d("$$$ " + dirName + "\t" + "mimeType='" + MIME_TYPE_FOLDER
//                + "' and trashed = false and title = '" + dirName + "'");
//        FileList files = request.execute();
//        final List<File> files1 = files.getFiles();
//        if (files1 != null) {
//            for (File file : files1) {
//                if (BuildConfig.DEBUG)
//                    DLog.d("getDirFile: found folder dir-->" + file.getId());
//                return file;
//            }
//        }
//        return null;
//    }
//
//
//    public File getUploadDirFile(String dirName, File parentFile) {
//
//        File rootFolder = null;
//
//        try {
//            if (parentFile != null) {
//                //Find folder by name
////                Drive.Children.List request = SERVICE.children().list(parentFile.getId());
////                ChildList children = request.execute();
////                DLog.d( "getDirFile: " + children.getItems().size());
////                for (ChildReference child : children.getItems()) {
////                    File childfile = mService.files().get(child.getId()).execute();
////                    System.out.makeLog("File Id: " + child.getId() + child.getKind() + "---" + childfile.getTitle());
////                    if (childfile.getTitle().equals(dirName))
////                        return childfile;
////                }
//                rootFolder = findFolderByName(parentFile);
//            } else {
//                rootFolder = getParentFolder(dirName);
//            }
//        } catch (Exception e) {
//            DLog.d("getUploadDirFile: " + e.getLocalizedMessage());
//        }
//
//        //If folder not found
//        if (rootFolder == null) {
//            DLog.d("ROOT_FOLDER: NULL");
//            try {
//                //create Folder
//                File fileMetadata = new File();
//                fileMetadata.setName(dirName);
//                fileMetadata.setMimeType(MIME_TYPE_FOLDER);
//                if (parentFile != null) {
//                    String folder_id = parentFile.getId();
//                    fileMetadata.setParents(Collections.singletonList(
//                            //new ParentReference().setId(folder_id)
//                            folder_id
//                            )
//                    );
//                    rootFolder = SERVICE.files()
//                            .create(fileMetadata)
//                            .setFields("id")
//                            .execute();
//                } else {
//                    rootFolder = SERVICE.files()
//                            .create(fileMetadata)
//                            .setFields("id")
//                            .execute();
//                }
//                return rootFolder;
//            } catch (IOException e) {
//                DLog.handleException(e);
//            }
//        }
//        return rootFolder;
//    }
//}

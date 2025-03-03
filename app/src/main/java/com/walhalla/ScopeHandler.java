//package com.walhalla;
//
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
//import com.google.android.gms.common.api.Scope;
//import com.google.api.services.drive.DriveScopes;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ScopeHandler {
//
////    //Checker
////    public static Set<Scope> aa() {
////        Set<Scope> SCOPES = new HashSet<>();
////        //SCOPES.add(new Scope("https://www.googleapis.com/auth/drive"));
////        SCOPES.add(new Scope(DriveScopes.DRIVE));
////        SCOPES.add(new Scope(DriveScopes.DRIVE_FILE));
////        SCOPES.add(new Scope(DriveScopes.DRIVE_READONLY));
////        SCOPES.add(new Scope(DriveScopes.DRIVE_APPDATA));
////        SCOPES.add(new Scope(DriveScopes.DRIVE_METADATA));
////        SCOPES.add(new Scope(DriveScopes.DRIVE_METADATA_READONLY));
////        //*** SCOPES.add(new Scope(DriveScopes.DRIVE_PHOTOS_READONLY));
////        return SCOPES;
////    }
//
//    //OAuth login
//    public static List<String> getScopes() {
//        List<String> aaa = new ArrayList<>();
//        aaa.add(DriveScopes.DRIVE);
//        //aaa.add(DriveScopes.DRIVE_FILE);
//        aaa.add(DriveScopes.DRIVE_READONLY);
//        aaa.add(DriveScopes.DRIVE_APPDATA);
//        //aaa.add(DriveScopes.DRIVE_METADATA);
//        aaa.add(DriveScopes.DRIVE_METADATA_READONLY);
//        //aaa.add(DriveScopes.DRIVE_PHOTOS_READONLY);
//        return aaa;
//    }
//
//    public static GoogleSignInOptions makeOptions() {
//        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestScopes(new Scope(DriveScopes.DRIVE))
//                //.requestScopes(new Scope(DriveScopes.DRIVE_FILE)) //Only read
//                .requestScopes(new Scope(DriveScopes.DRIVE_READONLY))
//                .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
//                //.requestScopes(new Scope(DriveScopes.DRIVE_METADATA))
//                .requestScopes(new Scope(DriveScopes.DRIVE_METADATA_READONLY))
//                //*** .requestScopes(new Scope(DriveScopes.DRIVE_PHOTOS_READONLY))
//                .requestEmail().build();
//    }
//}

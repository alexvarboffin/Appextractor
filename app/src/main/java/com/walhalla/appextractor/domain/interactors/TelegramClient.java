package com.walhalla.appextractor.domain.interactors;

import androidx.annotation.NonNull;

import com.walhalla.appextractor.utils.NetworkUtils;
import com.walhalla.ui.DLog;
import com.walhalla.ui.UConst;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TelegramClient {

    private static final String KEY_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; rv:50.0) Gecko/20100101 Firefox/50.0";
    private static final String BASE_URL = "https://api.telegram.org/bot";
    private static final String KEY_CHAT_ID = "chat_id";

    private final MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");

    public void sendLinkNotAsync(String apkChiefName, String apkChiefPackageName, SimpleMeta simpleMeta) {
        String apkChiefUrl = String.format(UConst.PLAY_STORE_URL, apkChiefPackageName, Locale.getDefault().getLanguage());

        OkHttpClient client = NetworkUtils.makeOkhttp();
        //String body = "chat_id=" + chatId + "&text=" + message;
        //RequestBody data;
        //data = MultipartBody.create(mediaType, body);

        try {
            JSONArray array = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("text", "{{ \uD83D\uDE4B " + simpleMeta.label + " on Google Play }}");
            jsonObject.put("url", UConst.GOOGLE_PLAY_CONSTANT + simpleMeta.packageName);

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("text", "{{ \uD83D\uDE80 " + apkChiefName + " on Google Play }}");
            jsonObject1.put("url", apkChiefUrl);

            array.put(jsonObject);
            array.put(jsonObject1);

            JSONArray array0 = new JSONArray();
            array0.put(array);

            JSONObject inline_keyboard = new JSONObject();
            inline_keyboard.put("inline_keyboard", array0);

            String json_inline_keyboard = String.valueOf(inline_keyboard);
            RequestBody body = new MultipartBody.Builder()// Создаем multipart запрос для отправки файла
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(KEY_CHAT_ID, chatId)
                    .addFormDataPart("text", apkChiefName)//optional
                    .addFormDataPart("reply_markup", json_inline_keyboard)//optional
                    //.addFormDataPart("caption", caption)
                    .addFormDataPart("disable_notification", "true")
                    //.addFormDataPart("photo", "photo.jpg", RequestBody.create(MediaType.parse("image/jpeg"), photo))
                    .build();
            Request request = new Request.Builder().addHeader("User-Agent", KEY_USER_AGENT) //optional
                    .url(BASE_URL + token + "/sendMessage")
                    .post(body)
                    .build();

//        Request request = new Request.Builder().addHeader("User-Agent", KEY_USER_AGENT) //optional
//                .url(BASE_URL + token + "/sendMessage")
//                .post(RequestBody.create(MediaType.parse("application/json"), json))
//                .build();
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String responseBody = response.body().string();
            } else {
                // Обработка ошибки
            }
        } catch (Exception e) {
            DLog.handleException(e);
        }
    }

//    public void sendLinkNotAsync(String apkChiefName, String apkChiefPackageName, SimpleMeta simpleMeta) {
//
//        String apkChiefUrl = String.format(Config.PLAY_STORE_URL, apkChiefPackageName, Locale.getDefault().getLanguage());
//
//        OkHttpClient client = NetworkUtils.makeOkhttp();
//        //String body = "chat_id=" + chatId + "&text=" + message;
//        //RequestBody data;
//        //data = MultipartBody.create(mediaType, body);
//        try {
//            //RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
//            JSONArray array = new JSONArray();
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("text", "{{ \uD83D\uDE4B " + simpleMeta.label + " on Google Play }}");
//            jsonObject.put("url", UConst.GOOGLE_PLAY_CONSTANT + simpleMeta.packageName);
//
//            JSONObject jsonObject1 = new JSONObject();
//            jsonObject1.put("text", "{{ \uD83D\uDE80 " + apkChiefName + " on Google Play }}");
//            jsonObject1.put("url", apkChiefUrl);
//
//            array.put(jsonObject);
//            array.put(jsonObject1);
//
//            JSONArray array0 = new JSONArray();
//            array0.put(array);
//
//            JSONObject inline_keyboard = new JSONObject();
//            inline_keyboard.put("inline_keyboard", array0);
//
//            String json_inline_keyboard = String.valueOf(inline_keyboard);
//            RequestBody body = new MultipartBody.Builder()// Создаем multipart запрос для отправки файла
//                    .setType(MultipartBody.FORM)
//                    .addFormDataPart(KEY_CHAT_ID, chatId)
//                    .addFormDataPart("text", apkChiefName)//optional
//                    .addFormDataPart("reply_markup", json_inline_keyboard)//optional
//                    .addFormDataPart("caption", caption)
//                    .addFormDataPart("disable_notification", "true")
//                    .addFormDataPart("photo", "photo.jpg", RequestBody.create(MediaType.parse("image/jpeg"), photo))
//                    .build();
//            Request request = new Request.Builder().addHeader("User-Agent", KEY_USER_AGENT) //optional
//                    .url(BASE_URL + token + "/sendMessage")
//                    .post(body)
//                    .build();
//
//            Response response = client.newCall(request).execute();
//            if (response.isSuccessful()) {
//                String responseBody = response.body().string();
//            } else {
//                // Обработка ошибки
//            }
//        } catch (Exception e) {
//            DLog.handleException(e);
//        }
//    }

    public interface TClientCallback {
        void onRetrievalFailed(Exception e);

        void onMessageRetrieved(String m);

        void onRetrievalFailed(String s);
    }

    private final String chatId;
    private final String token;

    public TelegramClient(String chatId, String token) {
        this.chatId = chatId;
        this.token = token;
    }

    public void sendMessage(String message) {


        OkHttpClient client = NetworkUtils.makeOkhttp();
        String body = "chat_id=" + chatId + "&text=" + message;


        RequestBody data;
        data = MultipartBody.create(mediaType, body);


        Request request = new Request.Builder()
                .addHeader("User-Agent", KEY_USER_AGENT) //optional
                .url(BASE_URL + token + "/sendMessage")
                .post(data) //call post
                .build();


        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                //System.out.makeLog("onFailure: " + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                //throw new IOException("Unexpected code " + response);
//                }
                //System.out.makeLog("onResponse: " + response.body().string());
            }

        });
    }


    public void sendDocumentNotAsync(String document, String caption,
                                     TClientCallback callback, CountingRequestBody.Listener c0) {

        final File doc = new File(document);

        try {
            OkHttpClient client = NetworkUtils.makeOkhttp();


//            String params = "chat_id=" + chatId + "&caption=" + caption +//"&document=" + document +
//                    "&disable_notification=true";
            //RequestBody data = MultipartBody.create(mediaType, params);


            MultipartBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("document", doc.getName(),
                            //"application/octet-stream"
                            RequestBody.create(mediaType, doc))
                    .addFormDataPart("chat_id", chatId)
                    .addFormDataPart("caption", caption)
                    .addFormDataPart("disable_notification", String.valueOf(true))
                    .build();

            // Decorate the request body to keep track of the upload progress
            CountingRequestBody body0 = new CountingRequestBody(body, c0);


            Request request = new Request.Builder()
                    .addHeader("User-Agent", KEY_USER_AGENT) //optional
                    .url(BASE_URL + token + "/sendDocument")
                    .post(body0) //call post
                    .build();
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                    callback.onRetrievalFailed(e);
//                }
//
//                @Override
//                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                    ResponseBody body = response.body();
//                    String json = body.string();
//
//                    if (!response.isSuccessful()) {
//                        callback.onRetrievalFailed("Telegram error! " + json);
//                    } else {
//                        callback.onMessageRetrieved(json);
//                    }
//                }
//            });

            String json = "";
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {//response.code() != 200
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        json = responseBody.string();
                    }
                    callback.onMessageRetrieved(json);
                } else {
                    String errorBodyString = null;
                    if (response.body() != null) {
                        errorBodyString = response.body().string();
                    }
                    callback.onRetrievalFailed("Telegram error Code: " + response.code() + " " + errorBodyString);
                }
            } catch (IOException e) {
                //SocketTimeoutException @@@ timeout
                //EACCES (Permission denied)
                DLog.d("---"+e.getMessage());
                callback.onRetrievalFailed("Telegram error code: " + e.getMessage());
                //callback.onRetrievalFailed("Telegram error code: " + e.getMessage() + " " + json);
                DLog.handleException(e);
            }
        } catch (Exception e) {
            DLog.handleException(e);
        }
    }


    public void sendPhoto(String photo, String caption) {


        OkHttpClient client = NetworkUtils.makeOkhttp();
        String body = "chat_id=" + chatId +
                "&caption=" + caption +
                "&photo=" + photo + "&disable_notification=true";


        RequestBody data;
        data = MultipartBody.create(mediaType, body);


        Request request = new Request.Builder()
                .addHeader("User-Agent", KEY_USER_AGENT) //optional
                .url(BASE_URL + token + "/sendPhoto")
                .post(data) //call post
                .build();


        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                DLog.d("onFailure: " + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //throw new IOException("Unexpected code " + response);
//                }
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    DLog.d("onResponse: " + responseBody.string());
                }
            }

        });
    }


    public void getUpdates(String message) {

        OkHttpClient client = NetworkUtils.makeOkhttp();
        String body = "chat_id=" + chatId + "&text=" + message;


        RequestBody data;
        data = MultipartBody.create(mediaType, body);


        Request request = new Request.Builder()
                .addHeader("User-Agent", KEY_USER_AGENT) //optional
                .url(BASE_URL + token + "/getUpdates")
                .post(data) //call post
                .build();


        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                DLog.d("onFailure: " + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                ResponseBody body = response.body();
                if (body != null) {
                    DLog.d("onResponse: " + body.string());
                }
            }

        });
    }

    public void sendMessageNotAsync(String message) {
        OkHttpClient client = NetworkUtils.makeOkhttp();
        String body = "chat_id=" + chatId + "&text=" + message;
        RequestBody data;
        data = MultipartBody.create(mediaType, body);
        Request request = new Request.Builder()
                .addHeader("User-Agent", KEY_USER_AGENT) //optional
                .url(BASE_URL + token + "/sendMessage")
                .post(data) //call post
                .build();
        try {
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                // Получаем тело ответа
                String responseBody = response.body().string();
            } else {
                // Обработка ошибки
            }
        } catch (IOException e) {
            DLog.handleException(e);
        }
    }


//        public void sendLocation(Location location) {
//            String token = ;
//            String chatId = ;
//
//
//            OkHttpClient client = NetworkUtils.makeOkhttp();
//            String body = "chat_id=" + chatId + "&latitude=" + location.getLatitude()
//                    + "&longitude=" + location.getLongitude()
//                    + "&disable_notification=true";
//
//
//            RequestBody data;
//            data = MultipartBody.create(mediaType, body);
//
//
//            Request request = new Request.Builder()
//                    .addHeader("User-Agent", KEY_USER_AGENT) //optional
//                    .url(BASE_URL + token + "/sendLocation")
//                    .post(data) //call post
//                    .build();
//
//
//            client.newCall(request).enqueue(new ExtractorViewCallback() {
//
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    System.out.makeLog("onFailure: " + e.getLocalizedMessage());
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
////                if (!response.isSuccessful()) {
//                    //throw new IOException("Unexpected code " + response);
////                }
//
//                    ResponseBody body = response.body();
//                    System.out.makeLog("onResponse: " + body.string());
//                }
//
//            });
//        }

}
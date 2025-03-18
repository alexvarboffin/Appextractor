package com.walhalla.appextractor.interactors

import com.walhalla.appextractor.utils.NetworkUtils.makeOkhttp
import com.walhalla.ui.DLog.d
import com.walhalla.ui.DLog.handleException
import com.walhalla.ui.UConst
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody

import okhttp3.Request

import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.Locale

class TelegramClient(private val chatId: String, private val token: String) {
    private val mediaType: MediaType =
        ("application/x-www-form-urlencoded; charset=UTF-8").toMediaType()

    fun sendLinkNotAsync(
        apkChiefName: String,
        apkChiefPackageName: String,
        simpleMeta: SimpleMeta
    ) {
        val apkChiefUrl =
            String.format(UConst.PLAY_STORE_URL, apkChiefPackageName, Locale.getDefault().language)

        val client = makeOkhttp()

        //String body = "chat_id=" + chatId + "&text=" + message;
        //RequestBody data;
        //data = MultipartBody.create(mediaType, body);
        try {
            val array = JSONArray()
            val jsonObject = JSONObject()
            jsonObject.put("text", "{{ \uD83D\uDE4B " + simpleMeta.label + " on Google Play }}")
            jsonObject.put("url", UConst.GOOGLE_PLAY_CONSTANT + simpleMeta.packageName)

            val jsonObject1 = JSONObject()
            jsonObject1.put("text", "{{ \uD83D\uDE80 $apkChiefName on Google Play }}")
            jsonObject1.put("url", apkChiefUrl)

            array.put(jsonObject)
            array.put(jsonObject1)

            val array0 = JSONArray()
            array0.put(array)

            val inline_keyboard = JSONObject()
            inline_keyboard.put("inline_keyboard", array0)

            val json_inline_keyboard = inline_keyboard.toString()
            val body: RequestBody =
                MultipartBody.Builder() // Создаем multipart запрос для отправки файла
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(KEY_CHAT_ID, chatId)
                    .addFormDataPart("text", apkChiefName) //optional
                    .addFormDataPart("reply_markup", json_inline_keyboard) //optional
                    //.addFormDataPart("caption", caption)
                    .addFormDataPart(
                        "disable_notification",
                        "true"
                    ) //.addFormDataPart("photo", "photo.jpg", RequestBody.create(MediaType.parse("image/jpeg"), photo))
                    .build()
            val request: Request =
                Request.Builder().addHeader("User-Agent", KEY_USER_AGENT) //optional
                    .url(BASE_URL + token + "/sendMessage")
                    .post(body)
                    .build()

            //        Request request = new Request.Builder().addHeader("User-Agent", KEY_USER_AGENT) //optional
//                .url(BASE_URL + token + "/sendMessage")
//                .post(RequestBody.create(MediaType.parse("application/json"), json))
//                .build();
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body!!.string()
            } else {
                // Обработка ошибки
            }
        } catch (e: Exception) {
            handleException(e)
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
    //                String responseBody = response.body.string();
    //            } else {
    //                // Обработка ошибки
    //            }
    //        } catch (Exception e) {
    //            DLog.handleException(e);
    //        }
    //    }
    interface TClientCallback {
        fun onRetrievalFailed(e: Exception)

        fun onMessageRetrieved(m: String)

        fun onRetrievalFailed(s: String)
    }

    fun sendMessage(message: String) {
        val client = makeOkhttp()
        val body = "chat_id=$chatId&text=$message"
        val data = RequestBody.create(mediaType, body)


        val request: Request = Request.Builder()
            .addHeader("User-Agent", KEY_USER_AGENT) //optional
            .url(BASE_URL + token + "/sendMessage")
            .post(data) //call post
            .build()


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //System.out.makeLog("onFailure: " + e.getLocalizedMessage());
            }

            override fun onResponse(call: Call, response: Response) {
                //throw new IOException("Unexpected code " + response);
//                }
                //System.out.makeLog("onResponse: " + response.body.string());
            }
        })
    }


    fun sendDocumentNotAsync(
        document: String, caption: String,
        callback: TClientCallback, c0: CountingRequestBody.Listener
    ) {
        val doc = File(document)

        try {
            val client = makeOkhttp()


            //            String params = "chat_id=" + chatId + "&caption=" + caption +//"&document=" + document +
//                    "&disable_notification=true";
            //RequestBody data = MultipartBody.create(mediaType, params);
            val body: MultipartBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "document", doc.name,  //"application/octet-stream"
                    RequestBody.create(mediaType, doc)
                )
                .addFormDataPart("chat_id", chatId)
                .addFormDataPart("caption", caption)
                .addFormDataPart("disable_notification", true.toString())
                .build()

            // Decorate the request body to keep track of the upload progress
            val body0 = CountingRequestBody(body, c0)


            val request: Request = Request.Builder()
                .addHeader("User-Agent", KEY_USER_AGENT) //optional
                .url(BASE_URL + token + "/sendDocument")
                .post(body0) //call post
                .build()

            //            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                    callback.onRetrievalFailed(e);
//                }
//
//                @Override
//                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                    ResponseBody body = response.body;
//                    String json = body.string();
//
//                    if (!response.isSuccessful()) {
//                        callback.onRetrievalFailed("Telegram error! " + json);
//                    } else {
//                        callback.onMessageRetrieved(json);
//                    }
//                }
//            });
            var json = ""
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) { //response.code() != 200
                    val responseBody = response.body
                    if (responseBody != null) {
                        json = responseBody.string()
                    }
                    callback.onMessageRetrieved(json)
                } else {
                    var errorBodyString: String? = null
                    if (response.body != null) {
                        errorBodyString = response.body!!.string()
                    }
                    callback.onRetrievalFailed("Telegram error Code: " + response.code + " " + errorBodyString)
                }
            } catch (e: IOException) {
                //SocketTimeoutException @@@ timeout
                //EACCES (Permission denied)
                d("---" + e.message)
                callback.onRetrievalFailed("Telegram error code: " + e.message)
                //callback.onRetrievalFailed("Telegram error code: " + e.getMessage() + " " + json);
                handleException(e)
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }


    fun sendPhoto(photo: String, caption: String) {
        val client = makeOkhttp()
        val body = "chat_id=" + chatId +
                "&caption=" + caption +
                "&photo=" + photo + "&disable_notification=true"
        val data = RequestBody.create(mediaType, body)


        val request: Request = Request.Builder()
            .addHeader("User-Agent", KEY_USER_AGENT) //optional
            .url(BASE_URL + token + "/sendPhoto")
            .post(data) //call post
            .build()


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                d("onFailure: " + e.localizedMessage)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                //throw new IOException("Unexpected code " + response);
//                }
                val responseBody = response.body
                if (responseBody != null) {
                    d("onResponse: " + responseBody.string())
                }
            }
        })
    }


    fun getUpdates(message: String) {
        val client = makeOkhttp()
        val body = "chat_id=$chatId&text=$message"
        val data = RequestBody.create(mediaType, body)


        val request: Request = Request.Builder()
            .addHeader("User-Agent", KEY_USER_AGENT) //optional
            .url(BASE_URL + token + "/getUpdates")
            .post(data) //call post
            .build()


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                d("onFailure: " + e.localizedMessage)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    throw IOException("Unexpected code $response")
                }

                val body = response.body
                if (body != null) {
                    d("onResponse: " + body.string())
                }
            }
        })
    }

    fun sendMessageNotAsync(message: String) {
        val client = makeOkhttp()
        val body = "chat_id=$chatId&text=$message"
        val data = RequestBody.create(mediaType, body)
        val request: Request = Request.Builder()
            .addHeader("User-Agent", KEY_USER_AGENT) //optional
            .url(BASE_URL + token + "/sendMessage")
            .post(data) //call post
            .build()
        try {
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                // Получаем тело ответа
                val responseBody = response.body!!.string()
            } else {
                // Обработка ошибки
            }
        } catch (e: IOException) {
            handleException(e)
        }
    } //        public void sendLocation(Location location) {
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
    //                    ResponseBody body = response.body;
    //                    System.out.makeLog("onResponse: " + body.string());
    //                }
    //
    //            });
    //        }


    companion object {
        private const val KEY_USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; rv:50.0) Gecko/20100101 Firefox/50.0"
        private const val BASE_URL = "https://api.telegram.org/bot"
        private const val KEY_CHAT_ID = "chat_id"
    }
}
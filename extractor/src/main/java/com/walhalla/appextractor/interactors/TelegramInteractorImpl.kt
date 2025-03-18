package com.walhalla.appextractor.interactors

import android.content.Context

import com.walhalla.appextractor.utils.Util
import com.walhalla.appextractor.interactors.TelegramClient.TClientCallback
import com.walhalla.extractor.R
import com.walhalla.threader.AbstractInteractor

import com.walhalla.ui.DLog.d
import com.walhalla.ui.DLog.getAppVersion
import com.walhalla.ui.DLog.handleException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger


class TelegramInteractorImpl @OptIn(DelicateCoroutinesApi::class) constructor(
//    mThreadExecutor: Executor?,
//    mMainThread: mMainThread?,

    var mThreadExecutor: CoroutineScope = CoroutineScope(Dispatchers.Default),
    var mMainThread: CoroutineScope = GlobalScope,


    private val telegramClient: TelegramClient
) :
    AbstractInteractor(mThreadExecutor, mMainThread) {

//    override fun run() {}


    interface Callback<T> {
        fun onMessageRetrieved(message: T)

        fun onRetrievalFailed(error: String)

        fun onProgress(file: File, percentage: Float, fileSize: String)

        fun onRetrievalFailed(e: Exception)

        fun hideProgressDialog()
    }


    fun sendDocument(
        total_extractedFiles: Map<SimpleMeta, List<File>>,
        context: Context,
        callback: Callback<String>
    ) {
        val eName = context.getString(R.string.app_name)
        val df: DateFormat = SimpleDateFormat("EEE, d MMM yyyy, HH:mm", Locale.getDefault())
        val date = df.format(Calendar.getInstance().time)
        val eNameFull = """
🚀$eName v${getAppVersion(context)}"""

        try {
            mThreadExecutor.launch {
                try {
                    var total = 0 //total file count
                    val errorCount = AtomicInteger() //total error count


                    for ((simpleMeta, files) in total_extractedFiles) {
                        val appName = simpleMeta.label
                        val packageName = simpleMeta.packageName

                        total = files.size
                        for (i in 0 until total) {
                            val file = files[i]
                            d("-->$i/$total $file ")

                            //{{ " + date + "}}
                            val caption = """
                                ${eNameFull.uppercase(Locale.getDefault())}
                                🙋$appName
                                ($packageName)
                                
                                🕒$date
                                """.trimIndent()

                            val fileSize = Util.getFileSizeMegaBytes(file)
                            telegramClient.sendDocumentNotAsync(
                                file.absolutePath,
                                caption,
                                object : TClientCallback {
                                    override fun onRetrievalFailed(e: Exception) {
                                        mMainThread.launch {
                                            errorCount.incrementAndGet()
                                            d("@@@@@@$e")
                                            //callback.hideProgressDialog();
                                            callback!!.onRetrievalFailed(e)
                                        }
                                    }

                                    override fun onMessageRetrieved(m: String) {
                                        mMainThread.launch {
                                            d("@@@@@@$m")
                                        }
                                    }

                                    override fun onRetrievalFailed(s: String) {
                                        mMainThread.launch {
                                            errorCount.incrementAndGet()
                                            d("@@@@@@$s")
                                            //callback.hideProgressDialog();
                                            callback!!.onRetrievalFailed(s)
                                        }
                                    }
                                }
                            ,object : CountingRequestBody.Listener{
                                    override fun onRequestProgress(
                                        bytesWritten: Long,
                                        contentLength: Long
                                    ) {
                                        if (callback != null) {
                                            mMainThread.launch {
                                                val percentage = 100f * bytesWritten / contentLength
                                                callback.onProgress(file, percentage, fileSize)
                                            }
                                        }
                                    }

                                })
                        }

                        if (errorCount.get() < total) {
                            telegramClient.sendLinkNotAsync(eName, context.packageName, simpleMeta)
                        }
                    }

                    callback.hideProgressDialog()

                    //callback.onMessageRetrieved(m);
                } catch (e: Exception) {
                    handleException(e)
                    if (callback != null) {
                        mMainThread.launch { callback.onRetrievalFailed(e) }
                    }
                }
            }
        } catch (e: Exception) {
            handleException(e)
            if (callback != null) {
                mMainThread.launch { callback.onRetrievalFailed(e) }
            }
        }
    }

    override fun cancel() {

    }
    //    public void sendDocument(final List<File> files, String caption, Callback<String> callback) {
    //
    //    }
}

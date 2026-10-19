package com.walhalla.appextractor

import android.app.Activity
import android.content.Context
import com.dropbox.core.android.Auth
import com.dropbox.core.v2.files.FileMetadata
import com.walhalla.appextractor.interactors.SimpleMeta
import com.walhalla.appextractor.interactors.TelegramClient
import com.walhalla.appextractor.interactors.TelegramInteractorImpl
import com.walhalla.appextractor.storage.LocalStorage
import com.walhalla.appextractor.task.UploadFileTask
import com.walhalla.db.DropboxClientFactory
import com.walhalla.extractor.R
import com.walhalla.ui.DLog.d
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.text.DateFormat

/**
 * Shared post-extract cloud upload (Dropbox / Telegram).
 * Used by legacy ExtractorFragment and Compose ExtractorViewModel.
 */
class CloudBackupCoordinator(
    private val context: Context,
    private val activity: Activity?,
    private val listener: Listener
) {
    interface Listener {
        fun onCloudMessage(success: Boolean, message: String)
        fun onCloudProgress(progress: Int, file: File?, fileSize: String?)
        fun onCloudBusy(busy: Boolean)
    }

    private var telegramInteractor: TelegramInteractorImpl? = null
    private var dropboxTask: UploadFileTask? = null

    fun handleSuccessExtracted(listMap: Map<SimpleMeta, List<File>>) {
        val mpm = LocalStorage.getInstance(context)

        if (mpm.enableDropBox()) {
            val act = activity
            if (act != null) {
                if (!mpm.hasDropBoxToken()) {
                    val key = context.getString(R.string.db_app_key)
                    if (key.isNotBlank() && key != "REPLACE_ME") {
                        Auth.startOAuth2Authentication(act, key)
                    }
                }
                if (mpm.hasDropBoxToken()) {
                    uploadToDropbox(mpm.dropboxAccessToken(), listMap)
                }
            }
        }

        if (mpm.enableTelegram()) {
            val chatId = mpm.telegramChatId().orEmpty()
            val token = mpm.telegramToken().orEmpty()
            if (!isValid(chatId) || !isValid(token)) return

            listener.onCloudBusy(true)
            val telegramClient = TelegramClient(chatId, token)
            telegramInteractor = TelegramInteractorImpl(
                mThreadExecutor = CoroutineScope(Dispatchers.Default),
                mMainThread = CoroutineScope(Dispatchers.Main),
                telegramClient
            )
            telegramInteractor!!.sendDocument(
                listMap,
                context,
                object : TelegramInteractorImpl.Callback<String> {
                    override fun onMessageRetrieved(message: String) {
                        listener.onCloudMessage(true, message)
                    }

                    override fun onRetrievalFailed(error: String) {
                        listener.onCloudBusy(false)
                        listener.onCloudMessage(false, error)
                    }

                    override fun onProgress(file: File, percentage: Float, fileSize: String) {
                        listener.onCloudProgress(percentage.toInt(), file, fileSize)
                    }

                    override fun onRetrievalFailed(e: Exception) {
                        listener.onCloudBusy(false)
                        listener.onCloudMessage(false, e.localizedMessage ?: e.toString())
                    }

                    override fun hideProgressDialog() {
                        listener.onCloudBusy(false)
                    }
                }
            )
        }
    }

    fun cancel() {
        telegramInteractor?.cancel()
        dropboxTask?.cancel(true)
        listener.onCloudBusy(false)
    }

    private fun uploadToDropbox(accessToken: String?, mFile: Map<SimpleMeta, List<File>>) {
        DropboxClientFactory.init(accessToken)
        val paths = ArrayList<String>()
        for ((_, files) in mFile) {
            for (file in files) {
                paths.add(file.absolutePath)
            }
        }
        listener.onCloudBusy(true)
        dropboxTask = UploadFileTask(
            context,
            DropboxClientFactory.getClient(),
            object : UploadFileTask.Callback {
                override fun onUploadComplete(data: List<FileMetadata>) {
                    listener.onCloudBusy(false)
                    dropboxTask = null
                    d("db-result: $data")
                    if (!data.isNullOrEmpty()) {
                        for (result in data) {
                            val message = String.format(
                                "%1\$s size %2\$s modified %3\$s",
                                result.pathLower,
                                result.size,
                                DateFormat.getDateTimeInstance().format(result.clientModified)
                            )
                            listener.onCloudMessage(true, message)
                        }
                    } else {
                        listener.onCloudMessage(true, "Dropbox: Success!")
                    }
                }

                override fun onError(e: Exception?) {
                    listener.onCloudBusy(false)
                    dropboxTask = null
                    listener.onCloudMessage(false, e?.localizedMessage ?: "Dropbox upload failed")
                }

                override fun progressUpdate(
                    progress: Array<Int>,
                    localFile: File,
                    fileSize: String
                ) {
                    val p = progress.firstOrNull() ?: 0
                    listener.onCloudProgress(p, localFile, fileSize)
                }
            }
        )
        dropboxTask!!.execute(paths)
    }

    private fun isValid(o: String?): Boolean = !o.isNullOrBlank()
}

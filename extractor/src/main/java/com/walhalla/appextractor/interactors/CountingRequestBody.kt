package com.walhalla.appextractor.interactors

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.Buffer
import okio.BufferedSink
import okio.ForwardingSink
import okio.Sink
import okio.buffer
import java.io.IOException

/**
 * Decorates an OkHttp request body to count the number of bytes written when writing it. Can
 * decorate any request body, but is most useful for tracking the upload progress of large
 * multipart requests.
 *
 * @author Leo Nikkilä
 */
class CountingRequestBody(protected var delegate: MultipartBody, protected var listener: Listener) :
    RequestBody() {
    protected var countingSink: CountingSink? = null

    override fun contentType(): MediaType? {
        return delegate.contentType()
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return delegate.contentLength()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        countingSink = CountingSink(sink)
        val bufferedSink = countingSink!!.buffer()

        delegate.writeTo(bufferedSink)

        bufferedSink.flush()
    }

    protected inner class CountingSink(delegate: Sink) : ForwardingSink(delegate) {
        private var bytesWritten: Long = 0

        @Throws(IOException::class)
        override fun write(source: Buffer, byteCount: Long) {
            super.write(source, byteCount)
            bytesWritten += byteCount
            listener.onRequestProgress(bytesWritten, contentLength())
        }
    }

    interface Listener {
        fun onRequestProgress(bytesWritten: Long, contentLength: Long)
    }
}

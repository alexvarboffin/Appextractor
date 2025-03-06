package com.walhalla.appextractor

import com.walhalla.ui.DLog.d
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel
import java.util.function.IntConsumer

class ReadableConsumerByteChannel(
    private val rbc: ReadableByteChannel,
    private val onRead: IntConsumer
) :
    ReadableByteChannel {
    private var totalByteRead = 0
    private val expectedSize: Long = 0

    @Throws(IOException::class)
    override fun read(dst: ByteBuffer): Int {
        var nRead: Int
        val progress: Double

        if ((rbc.read(dst).also { nRead = it }) > 0) {
            totalByteRead += nRead
            progress = if (expectedSize > 0) totalByteRead.toDouble() / expectedSize.toDouble() * 100.0 else -1.0
            //delegate.rbcProgressCallback(this, progress);
            d("@ --> $progress")
        }
        return nRead
    }

    //    @Override
    //    public int read(ByteBuffer dst) throws IOException {
    //        int nRead = rbc.read(dst);
    //        notifyBytesRead(nRead);
    //        return nRead;
    //    }
    //
    //    protected void notifyBytesRead(int nRead){
    //        if(nRead<=0) {
    //            return;
    //        }
    //        totalByteRead += nRead;
    //        onRead.accept(totalByteRead);
    //    }
    override fun isOpen(): Boolean {
        return rbc.isOpen
    }

    @Throws(IOException::class)
    override fun close() {
        rbc.close()
    }
}

package com.walhalla.appextractor;

import com.walhalla.ui.DLog;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.function.IntConsumer;


public class ReadableConsumerByteChannel implements ReadableByteChannel {

    private final ReadableByteChannel rbc;
    private final IntConsumer onRead;

    private int totalByteRead;
    private long expectedSize;

    public ReadableConsumerByteChannel(ReadableByteChannel rbc, IntConsumer onBytesRead) {
        this.rbc = rbc;
        this.onRead = onBytesRead;
    }

    public int read(ByteBuffer dst) throws IOException {
        int nRead;
        double progress;

        if ((nRead = rbc.read(dst)) > 0) {
            totalByteRead += nRead;
            progress = expectedSize > 0 ? (double) totalByteRead / (double) expectedSize * 100.0 : -1.0;
            //delegate.rbcProgressCallback(this, progress);
            DLog.d("@ --> " + progress);
        }
        return nRead;
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

    @Override
    public boolean isOpen() {
        return rbc.isOpen();
    }

    @Override
    public void close() throws IOException {
        rbc.close();
    }
}

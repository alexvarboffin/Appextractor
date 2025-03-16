package com.walhalla.appextractor

import com.walhalla.appextractor.core.RBCWrapperDelegate
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel

class DownloadProgressExample {
    //    public static void main( String[] args ) {
    //        new Downloader( "/tmp/foo.mp3", "http://foo.com/bar.mp3" );
    //    }
    //    private static final class Downloader implements RBCWrapperDelegate {
    //        public Downloader(String localPath, String remoteURL) {
    //            FileOutputStream fos;
    //            ReadableByteChannel rbc;
    //            URL url;
    //
    //            try {
    //                url = new URL(remoteURL);
    //                rbc = new RBCWrapper(Channels.newChannel(url.openStream()), contentLength(url), this);
    //                fos = new FileOutputStream(localPath);
    //                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    //            } catch (Exception e) {
    //                System.err.println("Uh oh: " + e.getMessage());
    //            }
    //        }
    //
    //        public void rbcProgressCallback(RBCWrapper rbc, double progress) {
    //            DLog.d(String.format("download progress %d bytes received, %.02f%%", rbc.getReadSoFar(), progress));
    //        }
    //
    //        private int contentLength(URL url) {
    //            HttpURLConnection connection;
    //            int contentLength = -1;
    //
    //            try {
    //                HttpURLConnection.setFollowRedirects(false);
    //
    //                connection = (HttpURLConnection) url.openConnection();
    //                connection.setRequestMethod("HEAD");
    //
    //                contentLength = connection.getContentLength();
    //                connection.disconnect();
    //            } catch (Exception e) {
    //            }
    //
    //            return contentLength;
    //        }
    //    }
    class RBCWrapper(
        private val rbc: ReadableByteChannel,
        private val expectedSize: Long,
        private val delegate: RBCWrapperDelegate?
    ) :
        ReadableByteChannel {
        var readSoFar: Long = 0
            private set

        @Throws(IOException::class)
        override fun close() {
            rbc.close()
        }

        override fun isOpen(): Boolean {
            return rbc.isOpen
        }

        @Throws(IOException::class)
        override fun read(bb: ByteBuffer): Int {
            var n: Int
            val progress: Double

            if ((rbc.read(bb).also { n = it }) > 0) {
                readSoFar += n.toLong()
                progress =
                    if (expectedSize > 0) readSoFar.toDouble() / expectedSize.toDouble() * 100.0 else -1.0

                delegate?.rbcProgressCallback(1, readSoFar.toInt(), this, progress)
            }

            return n
        }
    }
}

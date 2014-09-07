package co.fusionx.relay.dcc.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import co.fusionx.relay.dcc.pending.DCCPendingConnection;
import co.fusionx.relay.internal.sender.RelayDCCSender;
import co.fusionx.relay.util.DCCUtils;
import co.fusionx.relay.util.IOUtils;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class DCCGetConnection extends DCCFileConnection {

    private final File mFile;

    private final RelayDCCSender mRelayDCCSender;

    private CountDownLatch mCountDownLatch;

    public DCCGetConnection(final DCCPendingConnection pendingConnection,
            final DCCFileConversation conversation, final File file) {
        super(pendingConnection, conversation);

        mFile = file;
        mRelayDCCSender = new RelayDCCSender(conversation.getServer().getRelayPacketSender());
    }

    @Override
    protected void connect() {
        RandomAccessFile fileOutput = null;
        BufferedSource socketInput = null;
        BufferedSink socketOutput = null;

        try {
            // Create the random access file
            fileOutput = new RandomAccessFile(mFile, "rw");

            final long length = fileOutput.length();
            if (length == mPendingConnection.getSize()) {
                // The file is already fully downloaded - don't download it again
                setBytesTransferred(length);
                return;
            } else if (length != 0) {
                final boolean success = tryResume(length);
                fileOutput.seek(success ? length : 0);
            }

            // Create and connect to the socket
            final InetSocketAddress address = new InetSocketAddress(mPendingConnection.getIP(),
                    mPendingConnection.getPort());
            mSocket = new Socket();
            mSocket.setKeepAlive(true);
            mSocket.connect(address, 5000);

            // Create the socket source and sink
            socketInput = Okio.buffer(Okio.source(mSocket));
            socketOutput = Okio.buffer(Okio.sink(mSocket));

            // Create the input and output buffers
            final byte[] inBuffer = new byte[1024];
            final byte[] outBuffer = new byte[4];

            long bytesTransferred = fileOutput.getFilePointer();
            int bytesRead;
            while ((bytesRead = socketInput.read(inBuffer)) != -1) {
                // Write the retrieved data to the file
                fileOutput.write(inBuffer, 0, bytesRead);
                // Increment the transferred bytes
                bytesTransferred += bytesRead;
                // Set the progress
                setBytesTransferred(bytesTransferred);

                // Tell our peer how much data we have transferred
                DCCUtils.bytesTransferredToOutputBuffer(bytesTransferred, outBuffer);
                socketOutput.write(outBuffer);
                socketOutput.flush();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Close the socket streams
            IOUtils.closeQuietly(socketInput);
            IOUtils.closeQuietly(socketOutput);

            // Close the file and socket
            IOUtils.closeQuietly(fileOutput);
            IOUtils.closeQuietly(mSocket);
        }
    }

    private boolean tryResume(final long position) throws InterruptedException {
        mRelayDCCSender.requestResume(mPendingConnection.getDccRequestNick(),
                getFileName(), mPendingConnection.getPort(), position);

        mCountDownLatch = new CountDownLatch(1);
        return mCountDownLatch.await(10000, TimeUnit.MILLISECONDS);
    }

    // TODO - the position sent by peer should be used as an ultra check but simply ignore it for
    // now
    public void onResumeAccepted() {
        mCountDownLatch.countDown();
    }
}
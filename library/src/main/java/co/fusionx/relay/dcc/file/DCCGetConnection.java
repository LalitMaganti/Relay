package co.fusionx.relay.dcc.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Socket;

import co.fusionx.relay.dcc.DCCUtils;
import co.fusionx.relay.dcc.pending.DCCPendingConnection;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class DCCGetConnection extends DCCFileConnection {

    private final File mFile;

    public DCCGetConnection(final DCCPendingConnection pendingConnection,
            final DCCFileConversation fileConversation, final File file) {
        super(pendingConnection, fileConversation);

        mFile = file;
    }

    @Override
    protected void connect() {
        try {
            // Create and connect to the socket
            final InetSocketAddress address = new InetSocketAddress(mPendingConnection.getIP(),
                    mPendingConnection.getPort());
            mSocket = new Socket();
            mSocket.setKeepAlive(true);
            mSocket.connect(address, 5000);

            // Create the socket source and sink
            final BufferedSource socketInput = Okio.buffer(Okio.source(mSocket));
            final BufferedSink socketOutput = Okio.buffer(Okio.sink(mSocket));

            // Create the file - TODO - seek to the correct amount
            final RandomAccessFile fileOutput = new RandomAccessFile(mFile, "rw");
            fileOutput.seek(0);

            // Create the input and output buffers
            final byte[] inBuffer = new byte[1024];
            final byte[] outBuffer = new byte[4];

            int bytesTransferred = 0;
            int bytesRead;
            while ((bytesRead = socketInput.read(inBuffer)) != -1) {
                // Write the retrieved data to the file
                fileOutput.write(inBuffer, 0, bytesRead);
                // Increment the transferred bytes
                bytesTransferred += bytesRead;
                // Set the progress
                setProgress(bytesTransferred / mPendingConnection.getSize());

                // Tell our peer how much data we have transferred
                DCCUtils.bytesTransferredToOutputBuffer(bytesTransferred, outBuffer);
                socketOutput.write(outBuffer);
                socketOutput.flush();
            }
            // Close the socket streams
            socketInput.close();
            socketOutput.close();

            // Close the file and socket
            fileOutput.close();
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
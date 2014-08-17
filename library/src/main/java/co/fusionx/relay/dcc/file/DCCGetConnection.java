package co.fusionx.relay.dcc.file;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Socket;

import co.fusionx.relay.dcc.DCCConnection;
import co.fusionx.relay.dcc.DCCUtils;
import co.fusionx.relay.dcc.pending.DCCPendingConnection;
import okio.BufferedSource;
import okio.Okio;

public class DCCGetConnection extends DCCConnection {

    private final DCCFileConversation mFileConversation;

    private final File mFile;

    public DCCGetConnection(final DCCPendingConnection pendingConnection,
            final DCCFileConversation fileConversation, final File file) {
        super(pendingConnection);

        mFileConversation = fileConversation;
        mFile = file;
    }

    @Override
    protected void connect() {
        // mFileConversation.postAndStoreEvent(new DCCFileStartedEvent(this));

        try {
            final InetSocketAddress address = new InetSocketAddress(mPendingConnection.getIP(),
                    mPendingConnection.getPort());
            mSocket = new Socket();
            mSocket.setKeepAlive(true);
            mSocket.connect(address, 5000);

            final RandomAccessFile fileOutput = new RandomAccessFile(mFile, "rw");
            final BufferedSource socketInput = Okio.buffer(Okio.source(mSocket));
            final OutputStream socketOutput = mSocket.getOutputStream();
            fileOutput.seek(0);

            long bytesTransferred = 0;

            byte[] inBuffer = new byte[1024];
            byte[] outBuffer = new byte[4];
            int bytesRead;
            while ((bytesRead = socketInput.read(inBuffer)) != -1) {
                fileOutput.write(inBuffer, 0, bytesRead);
                bytesTransferred += bytesRead;

                DCCUtils.bytesTransferredToOutputBuffer(bytesTransferred, outBuffer);
                socketOutput.write(outBuffer);
                socketOutput.flush();
            }
            socketInput.close();
            socketOutput.close();

            fileOutput.close();
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
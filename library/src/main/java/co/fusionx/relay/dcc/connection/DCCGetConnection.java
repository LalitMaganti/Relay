package co.fusionx.relay.dcc.connection;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Socket;

import co.fusionx.relay.RelayServer;
import co.fusionx.relay.dcc.DCCUtils;
import co.fusionx.relay.dcc.pending.DCCPendingFileConnection;
import co.fusionx.relay.event.dcc.DCCFileStartedEvent;
import okio.BufferedSource;
import okio.Okio;

public class DCCGetConnection extends DCCFileConnection {

    private final File mFile;

    public DCCGetConnection(final RelayServer relayServer,
            final DCCPendingFileConnection pendingConnection, final File file) {
        super(relayServer, pendingConnection);

        mFile = file;
    }

    @Override
    protected void connect() {
        mServer.getServerEventBus().post(new DCCFileStartedEvent(this));

        try {
            final InetSocketAddress address = new InetSocketAddress(mPendingConnection.getIP(),
                    mPendingConnection.getPort());
            mSocket = new Socket();
            mSocket.setKeepAlive(true);
            mSocket.connect(address, 5000);

            final RandomAccessFile fileOutput = new RandomAccessFile(mFile.getCanonicalPath(),
                    "rw");
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof DCCGetConnection)) {
            return false;
        }

        final DCCGetConnection that = (DCCGetConnection) o;
        return mPendingConnection.equals(that.mPendingConnection) && mServer.equals(that.mServer);
    }

    @Override
    public int hashCode() {
        int result = mServer.hashCode();
        result = 31 * result + mPendingConnection.hashCode();
        return result;
    }
}
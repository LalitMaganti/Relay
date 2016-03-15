package co.fusionx.relay.dcc;

import java.io.IOException;
import java.net.Socket;

import co.fusionx.relay.dcc.pending.DCCPendingConnection;

public abstract class DCCConnection {

    protected final DCCPendingConnection mPendingConnection;

    protected Socket mSocket;

    private Thread mThread;

    public DCCConnection(final DCCPendingConnection pendingConnection) {
        mPendingConnection = pendingConnection;
    }

    public void startConnection() {
        mThread = new Thread(this::connect);
        mThread.start();
    }

    public void stopConnection() throws IOException {
        if (mThread.isAlive()) {
            mThread.interrupt();
        }
        if (mSocket != null && !mSocket.isClosed()) {
            mSocket.close();
        }
    }

    protected abstract void connect();
}
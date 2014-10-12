package co.fusionx.relay.core;

import java.io.IOException;
import java.net.Socket;

import co.fusionx.relay.internal.base.RelayDCCPendingConnection;

public abstract class DCCConnection {

    protected final RelayDCCPendingConnection mPendingConnection;

    protected Socket mSocket;

    private Thread mThread;

    public DCCConnection(final RelayDCCPendingConnection pendingConnection) {
        mPendingConnection = pendingConnection;
    }

    public void startConnection() {
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                connect();
            }
        });
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
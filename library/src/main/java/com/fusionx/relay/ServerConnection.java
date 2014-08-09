package com.fusionx.relay;

import android.os.Handler;
import android.os.HandlerThread;

import java.util.Collection;

/**
 * A wrapper thread class for the interesting {@link BaseConnection} class
 */
public class ServerConnection {

    private final RelayServer mServer;

    private final BaseConnection mBaseConnection;

    private final Handler mUiThreadHandler;

    private final Handler mServerCallHandler;

    private Thread mMainThread;

    private ConnectionStatus mStatus = ConnectionStatus.DISCONNECTED;

    ServerConnection(final ServerConfiguration configuration, final Handler handler,
            final Collection<String> ignoreList) {
        final HandlerThread handlerThread = new HandlerThread("ServerCalls");
        handlerThread.start();
        mServerCallHandler = new Handler(handlerThread.getLooper());

        mServer = new RelayServer(configuration, this, ignoreList);
        mBaseConnection = new BaseConnection(configuration, this);
        mUiThreadHandler = handler;
    }

    public Handler getServerCallHandler() {
        return mServerCallHandler;
    }

    public ConnectionStatus getStatus() {
        return mStatus;
    }

    void startConnection() {
        mMainThread = new Thread(() -> {
            try {
                mBaseConnection.connectToServer();
            } catch (final Exception ex) {
                mUiThreadHandler.post(() -> {
                    throw new RuntimeException(mBaseConnection.getCurrentLine(), ex);
                });
            }
        });
        mMainThread.start();
    }

    void stopConnection() {
        mServerCallHandler.post(() -> {
            if (mStatus == ConnectionStatus.CONNECTED) {
                mBaseConnection.stopConnection();
            } else if (mMainThread.isAlive()) {
                mMainThread.interrupt();
            }
            mBaseConnection.onStopped();
            mBaseConnection.closeSocket();
            mServer.onConnectionTerminated();
        });
    }

    RelayServer getServer() {
        return mServer;
    }

    void updateStatus(final ConnectionStatus newStatus) {
        mStatus = newStatus;
    }
}
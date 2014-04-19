package com.fusionx.relay.connection;

import com.fusionx.relay.ConnectionStatus;
import com.fusionx.relay.Server;
import com.fusionx.relay.ServerConfiguration;

import android.os.Handler;
import android.os.HandlerThread;

import java.util.List;

/**
 * A wrapper thread class for the interesting {@link BaseConnection} class
 */
public class ServerConnection {

    @SuppressWarnings("FieldCanBeLocal")
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                mBaseConnection.connectToServer();
            } catch (final Exception ex) {
                mUiThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        throw new RuntimeException(mBaseConnection.getCurrentLine(), ex);
                    }
                });
            }
        }
    };

    private final Server mServer;

    private final BaseConnection mBaseConnection;

    private final Handler mUiThreadHandler;

    private final Handler mServerCallHandler;

    private Thread mMainThread;

    private ConnectionStatus mStatus = ConnectionStatus.DISCONNECTED;

    ServerConnection(final ServerConfiguration configuration, final Handler handler,
            final List<String> ignoreList) {
        final HandlerThread handlerThread = new HandlerThread("ServerCalls");
        handlerThread.start();
        mServerCallHandler = new Handler(handlerThread.getLooper());

        mServer = new Server(configuration, this, ignoreList);
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
        mMainThread = new Thread(mRunnable);
        mMainThread.start();
    }

    void stopConnection() {
        mServerCallHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mStatus == ConnectionStatus.CONNECTED) {
                    mBaseConnection.stopConnection();
                } else if (mMainThread.isAlive()) {
                    mMainThread.interrupt();
                    mBaseConnection.closeSocket();
                }
            }
        });
    }

    Server getServer() {
        return mServer;
    }

    void updateStatus(final ConnectionStatus newStatus) {
        mStatus = newStatus;
    }
}
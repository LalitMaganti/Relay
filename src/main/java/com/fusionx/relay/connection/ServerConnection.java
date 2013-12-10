package com.fusionx.relay.connection;

import com.fusionx.relay.Server;
import com.fusionx.relay.ServerConfiguration;
import com.fusionx.relay.misc.InterfaceHolders;

import android.os.Handler;
import android.os.Looper;

public class ServerConnection extends Thread {

    private final Server mServer;

    private final BaseConnection mConnection;

    private final Handler mUiThreadHandler;

    private Handler mServerHandler;

    ServerConnection(final ServerConfiguration configuration, final Handler handler) {
        final Thread thread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                mServerHandler = new Handler();
                Looper.loop();
            }

            @Override
            public void interrupt() {
                super.interrupt();

                Looper.myLooper().quitSafely();
            }
        };
        thread.start();
        mServer = new Server(configuration.getTitle(), this);
        mConnection = new BaseConnection(configuration, mServer);
        mUiThreadHandler = handler;
    }

    @Override
    public void run() {
        try {
            mConnection.connectToServer();
        } catch (final Exception ex) {
            mUiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    throw new RuntimeException(ex);
                }
            });
        }
    }

    public void onDisconnect() {
        mServerHandler.post(new Runnable() {
            @Override
            public void run() {
                final String status = mServer.getStatus();
                if (status.equals(InterfaceHolders.getEventResponses().getConnectedStatus())) {
                    mConnection.onDisconnect();
                } else if (isAlive()) {
                    interrupt();
                    mConnection.closeSocket();
                }
            }
        });
    }

    public Handler getServerHandler() {
        return mServerHandler;
    }

    public Server getServer() {
        return mServer;
    }
}
package co.fusionx.relay.dcc.connection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;

import co.fusionx.relay.Conversation;
import co.fusionx.relay.RelayServer;
import co.fusionx.relay.Server;
import co.fusionx.relay.dcc.pending.DCCPendingConnection;

public abstract class DCCConnection implements Conversation {

    protected final RelayServer mServer;

    protected final DCCPendingConnection mPendingConnection;

    protected Socket mSocket;

    protected BufferedReader mBufferedReader;

    protected BufferedWriter mBufferedWriter;

    private Thread mThread;

    public DCCConnection(final RelayServer server, final DCCPendingConnection pendingConnection) {
        mServer = server;
        mPendingConnection = pendingConnection;
    }

    public void startConnection() {
        mThread = new Thread(this::connect);
        mThread.start();
    }

    public void stopConnection() {
        // TODO
    }

    protected abstract void connect();

    @Override
    public String getId() {
        return mPendingConnection.getDccRequestNick();
    }

    @Override
    public Server getServer() {
        return mServer;
    }

    // TODO - this has not been implemented
    @Override
    public boolean isValid() {
        return true;
    }
}
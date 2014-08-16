package co.fusionx.relay.dcc.connection;

import android.os.Handler;
import android.os.HandlerThread;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import co.fusionx.relay.RelayServer;
import co.fusionx.relay.dcc.pending.DCCPendingConnection;
import co.fusionx.relay.event.dcc.DCCChatSelfMessageEvent;
import co.fusionx.relay.event.dcc.DCCChatStartedEvent;
import co.fusionx.relay.event.dcc.DCCChatWorldMessageEvent;
import co.fusionx.relay.event.dcc.DCCEvent;
import co.fusionx.relay.misc.RelayConfigurationProvider;
import co.fusionx.relay.util.SocketUtils;

public class DCCChatConnection extends DCCConnection {

    private final Handler mCallHandler;

    private List<DCCEvent> mBuffer;

    public DCCChatConnection(final RelayServer server,
            final DCCPendingConnection pendingConnection) {
        super(server, pendingConnection);

        mBuffer = new ArrayList<>();

        final HandlerThread handlerThread = new HandlerThread("dccConnection");
        handlerThread.start();
        mCallHandler = new Handler(handlerThread.getLooper());
    }

    @Override
    protected void connect() {
        postAndStoreEvent(new DCCChatStartedEvent(this));

        try {
            mSocket = new Socket(mPendingConnection.getIP(), mPendingConnection.getPort());

            mBufferedReader = SocketUtils.getSocketBufferedReader(mSocket);
            mBufferedWriter = SocketUtils.getSocketBufferedWriter(mSocket);

            startParsing();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startParsing() throws IOException {
        String line;
        while ((line = mBufferedReader.readLine()) != null) {
            // For DCC there is no parsing required - the chat is simply the message
            postAndStoreEvent(new DCCChatWorldMessageEvent(this, line));
        }
    }

    private void postAndStoreEvent(final DCCEvent event) {
        mBuffer.add(event);
        mServer.getServerEventBus().post(event);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof DCCChatConnection)) {
            return false;
        }

        final DCCChatConnection that = (DCCChatConnection) o;
        return mPendingConnection.equals(that.mPendingConnection) && mServer.equals(that.mServer);
    }

    @Override
    public int hashCode() {
        int result = mServer.hashCode();
        result = 31 * result + mPendingConnection.hashCode();
        return result;
    }

    public List<DCCEvent> getBuffer() {
        return mBuffer;
    }

    public void sendMessage(final String message) {
        mCallHandler.post(() -> writeLine(message));

        if (RelayConfigurationProvider.getPreferences().isSelfEventHidden()) {
            return;
        }
        postAndStoreEvent(new DCCChatSelfMessageEvent(this, message));
    }

    private void writeLine(final String line) {
        try {
            mBufferedWriter.write(line + "\r\n");
            mBufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
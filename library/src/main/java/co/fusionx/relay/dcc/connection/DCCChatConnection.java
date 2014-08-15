package co.fusionx.relay.dcc.connection;

import java.io.IOException;
import java.net.Socket;

import co.fusionx.relay.RelayServer;
import co.fusionx.relay.dcc.pending.DCCPendingConnection;
import co.fusionx.relay.event.dcc.DCCWorldChatEvent;
import co.fusionx.relay.util.SocketUtils;

public class DCCChatConnection extends DCCConnection {

    public DCCChatConnection(final RelayServer server,
            final DCCPendingConnection pendingConnection) {
        super(server, pendingConnection);
    }

    @Override
    protected void connect() {
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
            mServer.getServerEventBus().post(new DCCWorldChatEvent(this, line));
        }
    }
}
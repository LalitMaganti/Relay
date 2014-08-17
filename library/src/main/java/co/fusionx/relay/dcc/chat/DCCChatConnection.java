package co.fusionx.relay.dcc.chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

import co.fusionx.relay.dcc.DCCConnection;
import co.fusionx.relay.dcc.event.chat.DCCChatStartedEvent;
import co.fusionx.relay.dcc.event.chat.DCCChatWorldMessageEvent;
import co.fusionx.relay.dcc.pending.DCCPendingConnection;
import co.fusionx.relay.util.SocketUtils;

class DCCChatConnection extends DCCConnection {

    private final DCCPendingConnection mPendingConversation;

    private final DCCChatConversation mDccChatConversation;

    protected BufferedReader mBufferedReader;

    protected BufferedWriter mBufferedWriter;

    public DCCChatConnection(final DCCPendingConnection pendingConversation,
            final DCCChatConversation dccChatConversation) {
        super(pendingConversation);

        mDccChatConversation = dccChatConversation;
        mPendingConversation = pendingConversation;
    }

    @Override
    protected void connect() {
        mDccChatConversation.postAndStoreEvent(new DCCChatStartedEvent(mDccChatConversation));

        try {
            mSocket = new Socket(mPendingConversation.getIP(), mPendingConversation.getPort());

            mBufferedReader = SocketUtils.getSocketBufferedReader(mSocket);
            mBufferedWriter = SocketUtils.getSocketBufferedWriter(mSocket);

            startParsing();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopConnection() throws IOException {
        super.stopConnection();

        if (mBufferedReader != null) {
            mBufferedReader.close();
        }
        if (mBufferedWriter != null) {
            mBufferedWriter.close();
        }
    }

    private void startParsing() throws IOException {
        String line;
        while ((line = mBufferedReader.readLine()) != null) {
            // For DCC there is no parsing required - the chat is simply the message
            mDccChatConversation.postAndStoreEvent
                    (new DCCChatWorldMessageEvent(mDccChatConversation, line));
        }
    }

    void writeLine(final String line) {
        try {
            mBufferedWriter.write(line + "\r\n");
            mBufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

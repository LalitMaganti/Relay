package co.fusionx.relay.internal.base;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

import co.fusionx.relay.event.chat.DCCChatWorldActionEvent;
import co.fusionx.relay.core.DCCConnection;
import co.fusionx.relay.event.chat.DCCChatStartedEvent;
import co.fusionx.relay.event.chat.DCCChatWorldMessageEvent;
import co.fusionx.relay.internal.parser.CTCPParser;
import co.fusionx.relay.util.SocketUtils;

class RelayDCCChatConnection extends DCCConnection {

    private final RelayDCCChatConversation mConversation;

    protected BufferedReader mBufferedReader;

    protected BufferedWriter mBufferedWriter;

    public RelayDCCChatConnection(final RelayDCCPendingConnection pendingConversation,
            final RelayDCCChatConversation conversation) {
        super(pendingConversation);

        mConversation = conversation;
    }

    @Override
    protected void connect() {
        mConversation.postEvent(new DCCChatStartedEvent(mConversation));

        try {
            mSocket = new Socket(mPendingConnection.getIP(), mPendingConnection.getPort());

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
            parseLine(line);
        }
    }

    private void parseLine(final String line) {
        if (CTCPParser.isCtcp(line)) {
            parseCtcp(line);
        } else {
            mConversation.postEvent(new DCCChatWorldMessageEvent(mConversation, line));
        }
    }

    private void parseCtcp(final String line) {
        final String message = line.substring(1, line.length() - 1);
        final String action = message.replace("ACTION ", "");
        mConversation.postEvent(new DCCChatWorldActionEvent(mConversation, action));
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

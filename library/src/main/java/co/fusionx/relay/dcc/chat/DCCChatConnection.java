package co.fusionx.relay.dcc.chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

import co.fusionx.relay.dcc.DCCConnection;
import co.fusionx.relay.dcc.event.chat.DCCChatStartedEvent;
import co.fusionx.relay.dcc.event.chat.DCCChatWorldMessageEvent;
import co.fusionx.relay.dcc.pending.DCCPendingConnection;
import co.fusionx.relay.parser.command.CtcpParser;
import co.fusionx.relay.util.SocketUtils;

class DCCChatConnection extends DCCConnection {

    private final DCCPendingConnection mPendingConversation;

    private final DCCChatConversation mConversation;

    protected BufferedReader mBufferedReader;

    protected BufferedWriter mBufferedWriter;

    public DCCChatConnection(final DCCPendingConnection pendingConversation,
            final DCCChatConversation conversation) {
        super(pendingConversation);

        mConversation = conversation;
        mPendingConversation = pendingConversation;
    }

    @Override
    protected void connect() {
        mConversation.postAndStoreEvent(new DCCChatStartedEvent(mConversation));

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
            parseLine(line);
        }
    }

    private void parseLine(final String line) {
        if (CtcpParser.isCtcp(line)) {
            parseCtcp(line);
        } else {
            mConversation.postAndStoreEvent(new DCCChatWorldMessageEvent(mConversation, line));
        }
    }

    private void parseCtcp(final String line) {
        final String message = line.substring(1, line.length() - 1);
        final String action = message.replace("ACTION ", "");
        mConversation.postAndStoreEvent(new DCCChatWorldActionEvent(mConversation, action));
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

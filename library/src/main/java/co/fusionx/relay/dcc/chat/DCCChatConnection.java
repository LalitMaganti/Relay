package co.fusionx.relay.dcc.chat;

import android.util.Pair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import co.fusionx.relay.base.FormatSpanInfo;
import co.fusionx.relay.dcc.DCCConnection;
import co.fusionx.relay.dcc.event.chat.DCCChatStartedEvent;
import co.fusionx.relay.dcc.event.chat.DCCChatWorldMessageEvent;
import co.fusionx.relay.dcc.pending.DCCPendingConnection;
import co.fusionx.relay.internal.parser.main.command.CTCPParser;
import co.fusionx.relay.util.SocketUtils;
import co.fusionx.relay.util.Utils;

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
        if (CTCPParser.isCtcp(line)) {
            parseCtcp(line);
        } else {
            final Pair<String, List<FormatSpanInfo>> messageAndColors =
                    Utils.parseAndStripColorsFromMessage(line);
            mConversation.postAndStoreEvent(new DCCChatWorldMessageEvent(mConversation,
                    messageAndColors.first, messageAndColors.second));
        }
    }

    private void parseCtcp(final String line) {
        final String message = line.substring(1, line.length() - 1);
        final String action = message.replace("ACTION ", "");
        final Pair<String, List<FormatSpanInfo>> actionAndColors =
                Utils.parseAndStripColorsFromMessage(action);
        mConversation.postAndStoreEvent(new DCCChatWorldActionEvent(mConversation,
                actionAndColors.first, actionAndColors.second));
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

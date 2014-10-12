package co.fusionx.relay.parser;

import java.util.List;

import co.fusionx.relay.internal.base.RelayDCCFileConnection;
import co.fusionx.relay.internal.base.RelayDCCFileConversation;
import co.fusionx.relay.internal.base.RelayDCCGetConnection;
import co.fusionx.relay.internal.base.RelayDCCPendingSendConnection;
import co.fusionx.relay.internal.base.RelayRelayDCCPendingChatConnection;
import co.fusionx.relay.internal.core.InternalDCCManager;
import co.fusionx.relay.event.server.DCCChatRequestEvent;
import co.fusionx.relay.event.server.DCCSendRequestEvent;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.util.DCCUtils;
import co.fusionx.relay.util.ParseUtils;

public class DCCParser {

    private final InternalServer mServer;

    private final InternalDCCManager mDCCManager;

    public DCCParser(final InternalServer server, final InternalDCCManager dccManager) {
        mServer = server;
        mDCCManager = dccManager;
    }

    // Examples of parsedArray lines
    // DCC CHAT chat <server IP int> <server port>
    // DCC FILE <file name> <server IP int> <server port> <file size>
    public void onParseCommand(final List<String> parsedArray, final String rawSource) {
        // Get the nick of the person who requested this
        final String nick = ParseUtils.getNickFromPrefix(rawSource);

        // Remove the DCC prefix
        parsedArray.remove(0);

        // Now get the type of DCC
        final String type = parsedArray.remove(0);

        // Remove the argument
        final String argument = parsedArray.remove(0);

        switch (type) {
            case "CHAT":
                parseChatCommand(nick, parsedArray);
                break;
            case "SEND":
                parseSendCommand(nick, argument, parsedArray);
                break;
            case "ACCEPT":
                parseAcceptCommand(nick, argument, parsedArray);
                break;
        }
    }

    private void parseAcceptCommand(final String nick, final String fileName,
            final List<String> parsedArray) {
        final int port = Integer.parseInt(parsedArray.remove(0));
        final long position = Long.parseLong(parsedArray.remove(0));

        final RelayDCCFileConversation conversation = mDCCManager.getFileConversation(nick);
        final RelayDCCFileConnection connection = conversation.getFileConnection(fileName);
        final RelayDCCGetConnection getConnection = (RelayDCCGetConnection) connection;

        getConnection.onResumeAccepted();
    }

    private void parseChatCommand(final String nick, final List<String> parsedArray) {
        // Retrieve the ip address as an integer and the port
        final long ipDecimal = Long.parseLong(parsedArray.remove(0));
        final int port = Integer.parseInt(parsedArray.remove(0));

        // Convert the address to a normal representation
        final String ipAddress = DCCUtils.ipDecimalToString(ipDecimal);

        // Send the event
        final RelayRelayDCCPendingChatConnection
                connection = new RelayRelayDCCPendingChatConnection(nick,
                mDCCManager, ipAddress, port, "chat", 0);
        mServer.postEvent(new DCCChatRequestEvent(mServer, connection));
    }

    private void parseSendCommand(final String nick, final String fileName,
            final List<String> parsedArray) {
        // Retrieve the ip address as an integer and the port
        final long ipDecimal = Long.parseLong(parsedArray.remove(0));
        final int port = Integer.parseInt(parsedArray.remove(0));

        // Convert the address to a normal representation
        final String ipAddress = DCCUtils.ipDecimalToString(ipDecimal);

        // Retrieve the file size - file size is optional from the spec
        final long size = parsedArray.size() > 0 ? Long.parseLong(parsedArray.remove(0)) : 0;

        // Send the event
        final RelayDCCPendingSendConnection connection = new RelayDCCPendingSendConnection(nick,
                mDCCManager, ipAddress, port, fileName, size);
        mServer.postEvent(new DCCSendRequestEvent(mServer, connection));
    }
}
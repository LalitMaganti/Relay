package co.fusionx.relay.parser.main.command;

import java.util.List;

import co.fusionx.relay.base.relay.RelayServer;
import co.fusionx.relay.dcc.file.DCCFileConnection;
import co.fusionx.relay.dcc.file.DCCFileConversation;
import co.fusionx.relay.dcc.file.DCCGetConnection;
import co.fusionx.relay.dcc.pending.DCCPendingChatConnection;
import co.fusionx.relay.dcc.pending.DCCPendingSendConnection;
import co.fusionx.relay.event.server.DCCChatRequestEvent;
import co.fusionx.relay.event.server.DCCSendRequestEvent;
import co.fusionx.relay.util.IRCUtils;
import co.fusionx.relay.util.ParseUtils;

public class DCCParser {

    private final RelayServer mServer;

    public DCCParser(final RelayServer server) {
        mServer = server;
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

        final DCCFileConversation conversation = mServer.getDCCManager().getFileConversation(nick);
        final DCCFileConnection connection = conversation.getFileConnection(fileName);
        final DCCGetConnection getConnection = (DCCGetConnection) connection;

        getConnection.onResumeAccepted();
    }

    private void parseChatCommand(final String nick, final List<String> parsedArray) {
        // Retrieve the ip address as an integer and the port
        final long ipDecimal = Long.parseLong(parsedArray.remove(0));
        final int port = Integer.parseInt(parsedArray.remove(0));

        // Convert the address to a normal representation
        final String ipAddress = IRCUtils.ipDecimalToString(ipDecimal);

        // Send the event
        final DCCPendingChatConnection connection = new DCCPendingChatConnection(nick,
                mServer.getDCCManager(), ipAddress, port, "chat", 0);
        mServer.postAndStoreEvent(new DCCChatRequestEvent(mServer, connection));
    }

    private void parseSendCommand(final String nick, final String fileName,
            final List<String> parsedArray) {
        // Retrieve the ip address as an integer and the port
        final long ipDecimal = Long.parseLong(parsedArray.remove(0));
        final int port = Integer.parseInt(parsedArray.remove(0));

        // Convert the address to a normal representation
        final String ipAddress = IRCUtils.ipDecimalToString(ipDecimal);

        // Retrieve the file size - file size is optional from the spec
        final long size = parsedArray.size() > 0 ? Long.parseLong(parsedArray.remove(0)) : 0;

        // Send the event
        final DCCPendingSendConnection connection = new DCCPendingSendConnection(nick,
                mServer.getDCCManager(), ipAddress, port, fileName, size);
        mServer.postAndStoreEvent(new DCCSendRequestEvent(mServer, connection));
    }
}
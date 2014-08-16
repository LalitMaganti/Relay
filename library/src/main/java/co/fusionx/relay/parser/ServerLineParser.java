package co.fusionx.relay.parser;

import org.apache.commons.lang3.StringUtils;

import android.util.Log;
import android.util.SparseArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import co.fusionx.relay.RelayServer;
import co.fusionx.relay.Server;
import co.fusionx.relay.bus.ServerCallHandler;
import co.fusionx.relay.constants.ServerCommands;
import co.fusionx.relay.constants.ServerReplyCodes;
import co.fusionx.relay.event.server.GenericServerEvent;
import co.fusionx.relay.event.server.ServerEvent;
import co.fusionx.relay.event.server.WhoisEvent;
import co.fusionx.relay.misc.CoreListener;
import co.fusionx.relay.parser.code.CodeParser;
import co.fusionx.relay.parser.command.CommandParser;
import co.fusionx.relay.parser.command.QuitParser;
import co.fusionx.relay.util.IRCUtils;

public class ServerLineParser {

    private static final int SERVER_COMMAND_SOURCE = 0;

    private static final int SERVER_COMMAND_OR_CODE = 1;

    private final Server mServer;

    private final Map<String, CommandParser> mCommandParserMap;

    private final SparseArray<CodeParser> mCodeParser;

    private ServerCallHandler mCallHandler;

    private String mLine;

    public ServerLineParser(final RelayServer server) {
        mServer = server;
        mCodeParser = CodeParser.getParserMap(server);
        mCommandParserMap = CommandParser.getParserMap(server);
    }

    /**
     * A loop which reads each line from the server as it is received and passes it on to parse
     *
     * @param reader      the reader associated with the server stream
     * @param callHandler the writer to write to the server
     */
    public void parseMain(final BufferedReader reader, final ServerCallHandler callHandler) throws
            IOException {
        mCallHandler = callHandler;

        while ((mLine = reader.readLine()) != null) {
            final boolean quit = parseLine();
            if (quit) {
                return;
            }
        }
    }

    public String getCurrentLine() {
        return mLine;
    }

    /**
     * Parses a line from the server
     *
     * @return a boolean indicating whether the server has disconnected
     */
    boolean parseLine() {
        final List<String> parsedArray = IRCUtils.splitRawLine(mLine, true);
        // For stupid servers that send blank lines - like seriously - why??
        if (parsedArray.isEmpty()) {
            return false;
        }

        final String command = parsedArray.get(0).toUpperCase(Locale.getDefault());
        switch (command) {
            case ServerCommands.PING:
                // Immediately respond & return
                final String source = parsedArray.get(1);
                CoreListener.respondToPing(mCallHandler, source);
                return false;
            case ServerCommands.ERROR:
                // We are finished - the server has kicked us
                // out for some reason
                return true;
            default:
                // Check if the second thing is a code or a command
                if (StringUtils.isNumeric(parsedArray.get(SERVER_COMMAND_OR_CODE))) {
                    onParseServerCode(mLine, parsedArray);
                } else {
                    return onParseServerCommand(parsedArray);
                }
                return false;
        }
    }

    // The server is sending a command to us - parse what it is
    private boolean onParseServerCommand(final List<String> parsedArray) {
        final String rawSource = parsedArray.get(SERVER_COMMAND_SOURCE);
        final String command = parsedArray.get(SERVER_COMMAND_OR_CODE)
                .toUpperCase(Locale.getDefault());

        // Parse the command
        final CommandParser parser = mCommandParserMap.get(command);
        // Silently fail if the parser is null - just ignore this line
        if (parser == null) {
            return false;
        }
        parser.onParseCommand(parsedArray, rawSource);

        if (parser instanceof QuitParser) {
            final QuitParser quitParser = (QuitParser) parser;
            return quitParser.isUserQuit();
        }
        return false;
    }

    private void onParseServerCode(final String rawLine, final List<String> parsedArray) {
        final int code = Integer.parseInt(parsedArray.get(SERVER_COMMAND_OR_CODE));

        // Pretty common across all the codes
        IRCUtils.removeFirstElementFromList(parsedArray, 3);
        final String message = parsedArray.get(0);

        if (ServerReplyCodes.genericCodes.contains(code)) {
            final ServerEvent event = new GenericServerEvent(mServer, message);
            mServer.getServerEventBus().postAndStoreEvent(event);
        } else if (ServerReplyCodes.whoisCodes.contains(code)) {
            final String response = IRCUtils.concatenateStringList(parsedArray);
            final WhoisEvent event = new WhoisEvent(mServer, response);
            mServer.getServerEventBus().postAndStoreEvent(event);
        } else if (ServerReplyCodes.doNothingCodes.contains(code)) {
            // Do nothing
        } else {
            final CodeParser parser = mCodeParser.get(code);
            if (parser == null) {
                Log.d("Relay", rawLine);
            } else {
                parser.onParseCode(code, parsedArray);
            }
        }
    }
}
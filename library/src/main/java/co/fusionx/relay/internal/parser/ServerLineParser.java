package co.fusionx.relay.internal.parser;

import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import co.fusionx.relay.event.server.GenericServerEvent;
import co.fusionx.relay.event.server.WhoisEvent;
import co.fusionx.relay.internal.constants.ServerReplyCodes;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.util.IRCUtils;
import co.fusionx.relay.util.ParseUtils;

public class ServerLineParser {

    private final InternalServer mServer;

    private final Map<String, CommandParser> mCommandParserMap;

    private final SparseArray<CodeParser> mCodeParser;

    private String mLine;

    @Inject
    public ServerLineParser(final InternalServer server, final SparseArray<CodeParser> codeParsers,
            final Map<String, CommandParser> commandParsers) {
        mServer = server;
        mCodeParser = codeParsers;
        mCommandParserMap = commandParsers;
    }

    /**
     * A loop which reads each line from the server as it is received and passes it on to parse
     *
     * @param reader the reader associated with the server stream
     */
    public void parseMain(final BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            final boolean quit = parseLine(line);
            if (quit) {
                return;
            }
        }
    }

    /**
     * Parses a line from the server
     *
     * @return a boolean indicating whether the server has disconnected
     */
    boolean parseLine(final String line) {
        // RFC2812 states that an empty line should be silently ignored
        if (TextUtils.isEmpty(line)) {
            return false;
        }
        mLine = line;
        Log.e("Relay", line);

        // Split the line
        final List<String> parsedArray = ParseUtils.splitRawLine(line, true);

        // Get the prefix if it exists
        final String prefix = ParseUtils.extractAndRemovePrefix(parsedArray);

        // Get the command
        final String command = parsedArray.remove(0);

        // Check if the command is a numeric code
        if (ParseUtils.isCommandCode(command)) {
            final int code = Integer.parseInt(command);
            parseServerCode(parsedArray, code);
        } else {
            return parserServerCommand(parsedArray, prefix, command);
        }
        return false;
    }

    // The server is sending a command to us - parse what it is
    private boolean parserServerCommand(final List<String> parsedArray, final String prefix,
            final String command) {
        // Parse the command
        final CommandParser parser = mCommandParserMap.get(command);
        // Silently fail if the parser is null - just ignore this line
        if (parser == null) {
            return false;
        }
        parser.onParseCommand(parsedArray, prefix);
        return parser.isUserQuit();
    }

    private void parseServerCode(final List<String> parsedArray, final int code) {
        parsedArray.remove(0); // Remove the target of the reply - ourselves

        if (ServerReplyCodes.genericCodes.contains(code)) {
            final String message = parsedArray.get(0);
            mServer.postEvent(new GenericServerEvent(mServer, message));
        } else if (ServerReplyCodes.whoisCodes.contains(code)) {
            final String response = IRCUtils.concatenateStringList(parsedArray);
            mServer.postEvent(new WhoisEvent(mServer, response));
        } else if (ServerReplyCodes.doNothingCodes.contains(code)) {
            // Do nothing
        } else {
            final CodeParser parser = mCodeParser.get(code);
            if (parser == null) {
                Log.d("Relay", mLine);
            } else {
                parser.onParseCode(parsedArray, code);
            }
        }
    }
}
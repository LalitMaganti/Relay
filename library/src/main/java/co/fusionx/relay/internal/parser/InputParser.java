package co.fusionx.relay.internal.parser;

import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import co.fusionx.relay.core.ConnectionConfiguration;
import co.fusionx.relay.event.server.GenericServerEvent;
import co.fusionx.relay.event.server.WhoisEvent;
import co.fusionx.relay.internal.constants.ServerReplyCodes;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.sender.CapSender;
import co.fusionx.relay.internal.sender.InternalSender;
import co.fusionx.relay.misc.NickStorage;
import co.fusionx.relay.util.IRCUtils;
import co.fusionx.relay.util.ParseUtils;

public class InputParser {

    private final ConnectionConfiguration mConfiguration;

    private final InternalServer mServer;

    private final Map<String, CommandParser> mCommandParserMap;

    private final SparseArray<CodeParser> mCodeParser;

    private final NickGenerator mNickGenerator;

    private String mLine;

    @Inject
    public InputParser(final ConnectionConfiguration configuration,
            final InternalServer server, final InternalSender internalSender,
            final CapSender capSender, final SparseArray<CodeParser> codeParsers,
            final Map<String, CommandParser> commandParsers) {
        mConfiguration = configuration;
        mServer = server;
        mCodeParser = codeParsers;
        mCommandParserMap = commandParsers;

        mNickGenerator = new NickGenerator(configuration);
    }

    /**
     * A loop which reads each line from the server as it is received and passes it on to parse
     *
     * @param reader the reader associated with the server stream
     */
    public void parseMain(final BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            final LineParseStatus lineParseStatus = parseLine(line);
            if (lineParseStatus.getStatus() == ParseStatus.QUIT) {
                return;
            }
        }
    }

    /**
     * Parses a line from the server
     *
     * @return a boolean indicating whether the server has disconnected
     */
    LineParseStatus parseLine(final String line) {
        // RFC2812 states that an empty line should be silently ignored
        if (TextUtils.isEmpty(line)) {
            return new LineParseStatus(ParseStatus.OTHER, null);
        }
        mLine = line;

        // Split the line
        final List<String> parsedArray = ParseUtils.splitRawLine(line, true);

        // Get the prefix if it exists
        final String prefix = ParseUtils.consumePrefixIfPresent(parsedArray);

        // Get the command
        final String command = parsedArray.remove(0);

        // Check if the command is a numeric code
        if (ParseUtils.isCommandCode(command)) {
            final int code = Integer.parseInt(command);
            return parseServerCode(parsedArray, code);
        } else {
            return parserServerCommand(parsedArray, prefix, command);
        }
    }

    // The server is sending a command to us - parse what it is
    private LineParseStatus parserServerCommand(final List<String> parsedArray, final String prefix,
            final String command) {
        // Parse the command
        final CommandParser parser = mCommandParserMap.get(command);
        if (parser == null) {
            // Silently fail if the parser is null - just ignore this line
            return new LineParseStatus(ParseStatus.OTHER, null);
        }
        parser.onParseCommand(parsedArray, prefix);

        return parser.isUserQuit()
                ? new LineParseStatus(ParseStatus.QUIT, null)
                : new LineParseStatus(ParseStatus.OTHER, null);
    }

    private LineParseStatus parseServerCode(final List<String> parsedArray, final int code) {
        final String target = parsedArray.remove(0); // Remove the target of the reply - ourselves
        switch (code) {
            case ServerReplyCodes.RPL_WELCOME:
                // We are now logged in.
                return new LineParseStatus(ParseStatus.NICK, target);
            case ServerReplyCodes.ERR_NICKNAMEINUSE:
                onNicknameInUse();
                break;
            case ServerReplyCodes.ERR_NONICKNAMEGIVEN:
                mServer.sendNick(mConfiguration.getNickStorage().getFirst());
                break;
            default:
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
        return new LineParseStatus(ParseStatus.OTHER, null);
    }

    private void onNicknameInUse() {
        mServer.sendNick(mNickGenerator.getNext());
    }

    public static enum ParseStatus {
        NICK,
        QUIT,
        OTHER
    }

    public static class LineParseStatus {

        private final ParseStatus mStatus;

        private final String mNick;

        public LineParseStatus(final ParseStatus status, final String nick) {
            mStatus = status;
            mNick = nick;
        }

        public String getNick() {
            return mNick;
        }

        public ParseStatus getStatus() {
            return mStatus;
        }
    }

    private static class NickGenerator {

        private final ConnectionConfiguration mConfiguration;

        private int mSuffix = 1;

        private int mIndex = 1;

        public NickGenerator(final ConnectionConfiguration configuration) {
            mConfiguration = configuration;
        }

        public String getNext() {
            final NickStorage nickStorage = mConfiguration.getNickStorage();
            if (mIndex < nickStorage.getNickCount()) {
                return nickStorage.getNickAtPosition(mIndex++);
            } else if (mConfiguration.isNickChangeable()) {
                return nickStorage.getFirst() + mSuffix++;
            }
            return null;
        }
    }
}
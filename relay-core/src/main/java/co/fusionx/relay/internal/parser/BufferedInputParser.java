package co.fusionx.relay.internal.parser;

import java.io.BufferedReader;
import java.io.IOException;

import javax.inject.Inject;

import co.fusionx.relay.configuration.ConnectionConfiguration;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.parser.InputParser;
import co.fusionx.relay.provider.NickProvider;

public class BufferedInputParser {

    private final ConnectionConfiguration mConfiguration;

    private final InputParser mInputParser;

    private final InternalServer mServer;

    private final NickGenerator mNickGenerator;

    private String mLine;

    @Inject
    public BufferedInputParser(final ConnectionConfiguration configuration,
            final InputParser inputParser,
            final InternalServer server) {
        mConfiguration = configuration;
        mInputParser = inputParser;
        mServer = server;

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
            mInputParser.parseLine(line);
            /*final LineParseStatus lineParseStatus = mInputParser.parseLine(line);
            if (lineParseStatus.getStatus() == ParseStatus.QUIT) {
                return;
            }*/
        }
    }

    /**
     * Parses a line from the server
     *
     * @return a boolean indicating whether the server has disconnected
     */
/*
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
        parser.parseCommand(parsedArray, prefix);

        return parser.isUserQuit()
                ? new LineParseStatus(ParseStatus.QUIT, null)
                : new LineParseStatus(ParseStatus.OTHER, null);
    }

    private LineParseStatus parseServerCode(final List<String> parsedArray, final int code) {
        final String target = parsedArray.remove(0); // Remove the target of the reply - ourselves
        switch (code) {
            case ReplyCodes.RPL_WELCOME:
                // We are now logged in.
                return new LineParseStatus(ParseStatus.NICK, target);
            case ReplyCodes.ERR_NICKNAMEINUSE:
                onNicknameInUse();
                break;
            case ReplyCodes.ERR_NONICKNAMEGIVEN:
                mServer.sendNick(mConfiguration.getNickStorage().getFirst());
                break;
            default:
                if (ReplyCodes.genericCodes.contains(code)) {
                    final String message = parsedArray.get(0);
                    mServer.postEvent(new GenericServerEvent(mServer, message));
                } else if (ReplyCodes.whoisCodes.contains(code)) {
                    final String response = IRCUtils.concatenateStringList(parsedArray);
                    mServer.postEvent(new WhoisEvent(mServer, response));
                } else if (ReplyCodes.doNothingCodes.contains(code)) {
                    // Do nothing
                } else {
                    final ReplyCodeParser parser = mCodeParser.get(code);
                    if (parser == null) {
                        Log.d("Relay", mLine);
                    } else {
                        parser.parseReplyCode(parsedArray, code);
                    }
                }
        }
        return new LineParseStatus(ParseStatus.OTHER, null);
    }
*/
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
            final NickProvider provider = mConfiguration.getNickProvider();
            if (mIndex < provider.getNickCount()) {
                return provider.getNickAtPosition(mIndex++);
            } else if (mConfiguration.isNickChangeable()) {
                return provider.getFirst() + mSuffix++;
            }
            return null;
        }
    }
}
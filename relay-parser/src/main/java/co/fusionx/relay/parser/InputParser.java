package co.fusionx.relay.parser;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

import co.fusionx.relay.util.ParseUtils;

public class InputParser {

    private final Map<String, CommandParser> mCommandParsers;

    private final Map<Integer, ReplyCodeParser> mReplyCodeParsers;

    private String mLine;

    public InputParser(final ParserProvider parserProvider) {
        mCommandParsers = parserProvider.getCommandParsers();
        mReplyCodeParsers = parserProvider.getReplyCodeParsers();
    }

    public void parseLine(final String line) {
        // RFC2812 states that an empty line should be silently ignored
        if (StringUtils.isEmpty(line)) {
            return;
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
            parseServerCode(parsedArray, code);
        } else {
            parserServerCommand(parsedArray, prefix, command);
        }
    }

    // The server is sending a command to us - parse what it is
    private void parserServerCommand(final List<String> parsedArray, final String prefix,
            final String command) {
        // Parse the command
        final CommandParser parser = mCommandParsers.get(command);
        if (parser == null) {
            // Silently fail if the parser is null - just ignore this line
            return;
        }
        parser.parseCommand(parsedArray, prefix);
    }

    private void parseServerCode(final List<String> parsedArray, final int code) {
        final String target = parsedArray.remove(0); // Remove the target of the reply - ourselves

        final ReplyCodeParser parser = mReplyCodeParsers.get(code);
        parser.parseReplyCode(parsedArray, code);
    }
}
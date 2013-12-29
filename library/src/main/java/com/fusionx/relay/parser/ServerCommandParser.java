package com.fusionx.relay.parser;

import com.fusionx.relay.Server;
import com.fusionx.relay.parser.command.CommandParser;
import com.fusionx.relay.parser.command.QuitParser;

import java.util.ArrayList;
import java.util.Map;

class ServerCommandParser {

    private Map<String, CommandParser> mParserMap;

    ServerCommandParser(final Server server) {
        mParserMap = CommandParser.getParserMap(server);
    }

    // The server is sending a command to us - parse what it is
    boolean onParseServerCommand(final ArrayList<String> parsedArray) {
        final String rawSource = parsedArray.get(0);
        final String command = parsedArray.get(1).toUpperCase();

        // Parse the command
        final CommandParser parser = mParserMap.get(command);
        parser.onParseCommand(parsedArray, rawSource);

        if (parser instanceof QuitParser) {
            final QuitParser quitParser = (QuitParser) parser;
            if (quitParser.isUserQuit()) {
                return false;
            }
        }
        return true;
    }
}

package co.fusionx.relay.internal.parser;

import java.util.List;

import co.fusionx.relay.parser.CommandParser;

public class ErrorCommandParser implements CommandParser {

    private boolean mQuit;

    public ErrorCommandParser() {
        mQuit = false;
    }

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        mQuit = true;
    }
}

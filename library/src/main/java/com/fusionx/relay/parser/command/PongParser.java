package com.fusionx.relay.parser.command;

import com.fusionx.relay.Server;

import java.util.List;

public class PongParser extends CommandParser {

    public PongParser(Server server) {
        super(server);
    }

    @Override
    public void onParseCommand(List<String> parsedArray, String rawSource) {
        // TODO - what should be done here?
    }
}
package com.fusionx.relay.parser.command;

import com.fusionx.relay.RelayServer;
import com.fusionx.relay.event.server.WallopsEvent;
import com.fusionx.relay.util.IRCUtils;

import java.util.List;

public class WallopsParser extends CommandParser {

    WallopsParser(final RelayServer server) {
        super(server);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String rawSource) {
        // It is unlikely that the person who sent the wallops is in one of our channels - simply
        // send the nick and message rather than the spruced up nick
        final String sendingNick = IRCUtils.getNickFromRaw(rawSource);
        final String message = parsedArray.get(2);

        getServerEventBus().postAndStoreEvent(new WallopsEvent(message, sendingNick));
    }
}
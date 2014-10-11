package co.fusionx.relay.internal.parser;

import java.util.List;

import co.fusionx.relay.event.server.WallopsEvent;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.parser.CommandParser;
import co.fusionx.relay.util.ParseUtils;

public class WallopsParser implements CommandParser {

    private final InternalServer mServer;

    public WallopsParser(final InternalServer server) {
        mServer = server;
    }

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        // It is unlikely that the person who sent the wallops is in one of our channels - simply
        // send the nick and message rather than the spruced up nick
        final String sendingNick = ParseUtils.getNickFromPrefix(prefix);
        final String message = parsedArray.get(0);

        mServer.postEvent(new WallopsEvent(mServer, message, sendingNick));
    }
}
package co.fusionx.relay.parser.command;

import java.util.List;

import co.fusionx.relay.RelayServer;
import co.fusionx.relay.event.server.WallopsEvent;
import co.fusionx.relay.util.IRCUtils;

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

        mServer.postAndStoreEvent(new WallopsEvent(mServer, message, sendingNick));
    }
}
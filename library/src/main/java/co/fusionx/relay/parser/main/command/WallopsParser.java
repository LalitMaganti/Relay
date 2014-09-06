package co.fusionx.relay.parser.main.command;

import java.util.List;

import co.fusionx.relay.base.relay.RelayServer;
import co.fusionx.relay.event.server.WallopsEvent;
import co.fusionx.relay.util.ParseUtils;

public class WallopsParser extends CommandParser {

    WallopsParser(final RelayServer server) {
        super(server);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String prefix) {
        // It is unlikely that the person who sent the wallops is in one of our channels - simply
        // send the nick and message rather than the spruced up nick
        final String sendingNick = ParseUtils.getNickFromPrefix(prefix);
        final String message = parsedArray.get(0);

        mServer.postAndStoreEvent(new WallopsEvent(mServer, message, sendingNick));
    }
}
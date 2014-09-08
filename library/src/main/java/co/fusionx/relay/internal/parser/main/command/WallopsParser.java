package co.fusionx.relay.internal.parser.main.command;

import java.util.List;

import co.fusionx.relay.internal.base.RelayQueryUserGroup;
import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.event.server.WallopsEvent;
import co.fusionx.relay.internal.base.RelayUserChannelGroup;
import co.fusionx.relay.util.ParseUtils;

public class WallopsParser extends CommandParser {

    public WallopsParser(final RelayServer server,
            final RelayUserChannelGroup userChannelInterface,
            final RelayQueryUserGroup queryManager) {
        super(server, userChannelInterface, queryManager);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String prefix) {
        // It is unlikely that the person who sent the wallops is in one of our channels - simply
        // send the nick and message rather than the spruced up nick
        final String sendingNick = ParseUtils.getNickFromPrefix(prefix);
        final String message = parsedArray.get(0);

        mServer.getBus().post(new WallopsEvent(mServer, message, sendingNick));
    }
}
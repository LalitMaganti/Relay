package co.fusionx.relay.internal.parser;

import java.util.List;

import co.fusionx.relay.event.server.WallopsEvent;
import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.util.ParseUtils;

public class WallopsParser extends CommandParser {

    public WallopsParser(final InternalServer server,
            final InternalUserChannelGroup userChannelInterface,
            final InternalQueryUserGroup queryManager) {
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
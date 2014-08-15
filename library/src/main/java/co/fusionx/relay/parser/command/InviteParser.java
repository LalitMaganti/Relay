package co.fusionx.relay.parser.command;

import co.fusionx.relay.RelayServer;
import co.fusionx.relay.event.server.InviteEvent;
import co.fusionx.relay.event.server.ServerEvent;
import co.fusionx.relay.util.IRCUtils;

import java.util.List;

class InviteParser extends CommandParser {

    public InviteParser(final RelayServer server) {
        super(server);
    }

    @Override
    public void onParseCommand(List<String> parsedArray, String rawSource) {
        final String invitingNick = IRCUtils.getNickFromRaw(rawSource);
        final String invitedNick = parsedArray.get(2);
        if (mServer.getUser().isNickEqual(invitedNick)) {
            final String channelName = parsedArray.get(3);
            final ServerEvent event = new InviteEvent(channelName, invitingNick);
            mServerEventBus.postAndStoreEvent(event);
        } else {
            // This is impossible - breaks RFC if it occurs - just ignore it
        }
    }
}
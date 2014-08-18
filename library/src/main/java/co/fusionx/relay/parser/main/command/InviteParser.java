package co.fusionx.relay.parser.main.command;

import java.util.List;

import co.fusionx.relay.base.relay.RelayServer;
import co.fusionx.relay.event.server.InviteEvent;
import co.fusionx.relay.util.IRCUtils;

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
            mServer.postAndStoreEvent(new InviteEvent(mServer, channelName, invitingNick));
        } else {
            // This is impossible - breaks RFC if it occurs - just ignore it
        }
    }
}
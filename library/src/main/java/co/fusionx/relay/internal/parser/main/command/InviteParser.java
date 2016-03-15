package co.fusionx.relay.internal.parser.main.command;

import java.util.List;

import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.event.server.InviteEvent;
import co.fusionx.relay.util.ParseUtils;

class InviteParser extends CommandParser {

    public InviteParser(final RelayServer server) {
        super(server);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String prefix) {
        final String invitingNick = ParseUtils.getNickFromPrefix(prefix);
        final String invitedNick = parsedArray.get(0);
        if (mServer.getUser().isNickEqual(invitedNick)) {
            final String channelName = parsedArray.get(1);
            mServer.postAndStoreEvent(new InviteEvent(mServer, channelName, invitingNick));
        } else {
            // This is impossible - breaks RFC if it occurs - just ignore it
        }
    }
}
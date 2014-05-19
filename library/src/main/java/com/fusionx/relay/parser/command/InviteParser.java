package com.fusionx.relay.parser.command;

import com.fusionx.relay.Server;
import com.fusionx.relay.event.server.InviteEvent;
import com.fusionx.relay.event.server.ServerEvent;
import com.fusionx.relay.util.IRCUtils;

import java.util.List;

class InviteParser extends CommandParser {

    public InviteParser(Server server) {
        super(server);
    }

    @Override
    public void onParseCommand(List<String> parsedArray, String rawSource) {
        final String invitingNick = IRCUtils.getNickFromRaw(rawSource);
        final String invitedNick = parsedArray.get(2);
        if (invitedNick.equals(getServer().getUser().getNick().getNickAsString())) {
            final String channelName = parsedArray.get(3);
            final ServerEvent event = new InviteEvent(channelName, invitingNick);
            getServerEventBus().postAndStoreEvent(event);
        } else {
            // This is impossible - breaks RFC if it occurs - just ignore it
        }
    }
}
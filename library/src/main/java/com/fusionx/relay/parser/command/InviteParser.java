package com.fusionx.relay.parser.command;

import com.fusionx.relay.Server;
import com.fusionx.relay.util.IRCUtils;

import java.util.List;

public class InviteParser extends CommandParser {

    public InviteParser(Server server) {
        super(server);
    }

    @Override
    public void onParseCommand(List<String> parsedArray, String rawSource) {
        final String invitingNick = IRCUtils.getNickFromRaw(rawSource);
        if (parsedArray.get(2).equals(mServer.getUser().getNick())) {
            final String channelName = parsedArray.get(3);
            //mServerEventBus.sendInviteEvent(mServer, channelName);
        } else {
            // TODO - fix up what should happen here
        }
    }
}
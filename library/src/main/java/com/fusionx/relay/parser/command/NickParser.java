package com.fusionx.relay.parser.command;

import com.fusionx.relay.AppUser;
import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.event.channel.ChannelEvent;
import com.fusionx.relay.event.channel.ChannelNickChangeEvent;
import com.fusionx.relay.event.channel.ChannelWorldNickChangeEvent;
import com.fusionx.relay.event.server.ServerNickChangeEvent;
import com.fusionx.relay.nick.Nick;
import com.fusionx.relay.util.IRCUtils;

import java.util.List;

class NickParser extends CommandParser {

    private static final int NEW_NICK_INDEX = 2;

    public NickParser(final Server server) {
        super(server);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String rawSource) {
        final String oldRawNick = IRCUtils.getNickFromRaw(rawSource);
        final boolean appUser = getServer().getUser().isNickEqual(oldRawNick);
        final ChannelUser user = appUser
                ? getServer().getUser()
                : getUserChannelInterface().getUser(oldRawNick);

        // The can happen in cases where gave a nick to the server but it ignored this nick and
        // gave use another one instead. Then half way through the server notice phase it
        // randomly decides to change our nick from the one we provided to the one which we have
        // already been given and using - simply ignore this bad nick change - Miau is a BNC
        // which displays this behaviour
        if (user == null) {
            // TODO - maybe send an event which indicates an unexpected case has occurred
            return;
        }

        final String newNick = parsedArray.get(NEW_NICK_INDEX);
        final Nick oldNick = user.getNick();
        user.setNick(newNick);

        if (appUser) {
            final ServerNickChangeEvent event = new ServerNickChangeEvent(oldNick, user);
            getServerEventBus().postAndStoreEvent(event);
        }

        for (final Channel channel : user.getChannels()) {
            final ChannelEvent event;
            if (appUser) {
                event = new ChannelNickChangeEvent(channel, oldNick, (AppUser) user);
            } else {
                event = new ChannelWorldNickChangeEvent(channel, oldNick, user);
            }
            getServerEventBus().postAndStoreEvent(event, channel);
        }
    }
}
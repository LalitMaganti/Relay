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

import java.util.Collection;
import java.util.List;

class NickParser extends CommandParser {

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
        final Collection<Channel> channels = user.getChannels();

        final Nick oldNick = user.getNick();
        user.setNick(parsedArray.get(2));

        if (appUser) {
            final ServerNickChangeEvent event = new ServerNickChangeEvent(oldNick, user);
            getServerEventBus().postAndStoreEvent(event);
        }

        for (final Channel channel : channels) {
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
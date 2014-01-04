package com.fusionx.relay.parser.command;

import com.fusionx.relay.AppUser;
import com.fusionx.relay.Channel;
import com.fusionx.relay.Server;
import com.fusionx.relay.WorldUser;
import com.fusionx.relay.event.channel.ChannelEvent;
import com.fusionx.relay.event.channel.NickChangeEvent;
import com.fusionx.relay.event.channel.WorldNickChangeEvent;
import com.fusionx.relay.event.server.ServerNickChangeEvent;
import com.fusionx.relay.util.IRCUtils;

import java.util.Collection;
import java.util.List;

public class NickParser extends CommandParser {

    public NickParser(final Server server) {
        super(server);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String rawSource) {
        final String oldRawNick = IRCUtils.getNickFromRaw(rawSource);
        final WorldUser user = mUserChannelInterface.getUserIfExists(oldRawNick);
        final Collection<Channel> channels = user.getChannels();

        final String oldNick = user.getColorfulNick();

        user.setNick(parsedArray.get(2));

        if (user instanceof AppUser) {
            final ServerNickChangeEvent event = new ServerNickChangeEvent(oldNick, user);
            mServerEventBus.postAndStoreEvent(event, mServer);
        }

        for (final Channel channel : channels) {
            final ChannelEvent event;
            if (user instanceof AppUser) {
                event = new NickChangeEvent(channel, oldNick, (AppUser) user);
            } else {
                event = new WorldNickChangeEvent(channel, oldNick, user);
            }
            user.onUpdateNick(channel);
            mServerEventBus.postAndStoreEvent(event, channel);
        }
    }
}
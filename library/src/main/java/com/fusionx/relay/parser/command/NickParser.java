package com.fusionx.relay.parser.command;

import com.fusionx.relay.AppUser;
import com.fusionx.relay.RelayChannel;
import com.fusionx.relay.RelayChannelUser;
import com.fusionx.relay.RelayServer;
import com.fusionx.relay.event.channel.ChannelEvent;
import com.fusionx.relay.event.channel.ChannelNickChangeEvent;
import com.fusionx.relay.event.channel.ChannelWorldNickChangeEvent;
import com.fusionx.relay.event.server.ServerNickChangeEvent;
import com.fusionx.relay.Nick;
import com.fusionx.relay.util.IRCUtils;
import com.fusionx.relay.util.LogUtils;

import java.util.List;

import java8.util.Optional;
import java8.util.stream.StreamSupport;

class NickParser extends CommandParser {

    private static final int NEW_NICK_INDEX = 2;

    public NickParser(final RelayServer server) {
        super(server);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String rawSource) {
        final String oldRawNick = IRCUtils.getNickFromRaw(rawSource);
        final boolean appUser = mServer.getUser().isNickEqual(oldRawNick);
        final Optional<RelayChannelUser> optUser = appUser
                ? Optional.of(mServer.getUser())
                : mUserChannelInterface.getUser(oldRawNick);

        // The can happen in cases where gave a nick to the server but it ignored this nick and
        // gave use another one instead. Then half way through the server notice phase it
        // randomly decides to change our nick from the one we provided to the one which we have
        // already been given and using - simply ignore this bad nick change - Miau is a BNC
        // which displays this behaviour
        LogUtils.logOptionalBug(optUser);
        optUser.ifPresent(user -> {
            final String newNick = parsedArray.get(NEW_NICK_INDEX);
            final Nick oldNick = user.getNick();
            user.setNick(newNick);

            if (appUser) {
                final ServerNickChangeEvent event = new ServerNickChangeEvent(oldNick, user);
                mServerEventBus.postAndStoreEvent(event);
            }

            StreamSupport.stream(user.getChannels()).forEach(channel -> {
                final ChannelEvent event  = appUser
                        ? new ChannelNickChangeEvent(channel, oldNick, (AppUser) user)
                        : new ChannelWorldNickChangeEvent(channel, oldNick, user);
                mServerEventBus.postAndStoreEvent(event, channel);
            });
        });
    }
}
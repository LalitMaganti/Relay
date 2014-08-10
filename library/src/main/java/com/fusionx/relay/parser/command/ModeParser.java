package com.fusionx.relay.parser.command;

import com.fusionx.relay.RelayMainUser;
import com.fusionx.relay.RelayChannel;
import com.fusionx.relay.RelayChannelUser;
import com.fusionx.relay.RelayServer;
import com.fusionx.relay.constants.UserLevel;
import com.fusionx.relay.event.channel.ChannelEvent;
import com.fusionx.relay.event.channel.ChannelModeEvent;
import com.fusionx.relay.event.channel.ChannelUserLevelChangeEvent;
import com.fusionx.relay.event.channel.ChannelWorldLevelChangeEvent;
import com.fusionx.relay.util.IRCUtils;
import com.fusionx.relay.util.LogUtils;

import java.util.List;

import java8.util.Optional;

class ModeParser extends CommandParser {

    public ModeParser(final RelayServer server) {
        super(server);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String rawSource) {
        final String sendingUser = IRCUtils.getNickFromRaw(rawSource);
        final String recipient = parsedArray.get(2);
        final String mode = parsedArray.get(3);

        if (RelayChannel.isChannelPrefix(recipient.charAt(0))) {
            // The recipient is a channel (i.e. the mode of a user in the channel is being changed
            // or possibly the mode of the channel itself)
            final Optional<RelayChannel> optChannel = mUserChannelInterface.getChannel(recipient);

            LogUtils.logOptionalBug(optChannel);
            optChannel.ifPresent(channel -> {
                final int messageLength = parsedArray.size();
                if (messageLength == 4) {
                    // User not specified - therefore channel mode is being changed
                    // TODO - implement this?
                } else if (messageLength == 5) {
                    // User specified - therefore user mode in channel is being changed
                    onUserModeInChannel(parsedArray, sendingUser, channel, mode);
                }
            });
        } else {
            // A user is changing a mode about themselves
            // TODO - implement this?
        }
    }

    private void onUserModeInChannel(final List<String> parsedArray, final String sendingNick,
            final RelayChannel channel, final String mode) {
        final String source = parsedArray.get(4);
        final String nick = IRCUtils.getNickFromRaw(source);
        final boolean appUser = mServer.getUser().isNickEqual(nick);

        final Optional<RelayChannelUser> optUser;
        optUser = appUser ? Optional.of(mServer.getUser()) : mUserChannelInterface.getUser(nick);
        final Optional<RelayChannelUser> optSending = mUserChannelInterface.getUser(sendingNick);

        // Nullity can occur when a ban is being added/removed on a whole range using wildcards
        if (optUser.isPresent()) {
            final RelayChannelUser user = optUser.get();
            final UserLevel levelEnum = user.onModeChange(channel, mode);

            final ChannelEvent event = appUser
                    ? new ChannelUserLevelChangeEvent(channel, mode, (RelayMainUser) user, levelEnum,
                    optSending, sendingNick)
                    : new ChannelWorldLevelChangeEvent(channel, mode, user, levelEnum,
                            optSending, sendingNick);
            mServerEventBus.postAndStoreEvent(event, channel);
        } else {
            final ChannelEvent event = new ChannelModeEvent(channel, optSending, sendingNick,
                    source, mode);
            mServerEventBus.postAndStoreEvent(event, channel);
        }
    }
}
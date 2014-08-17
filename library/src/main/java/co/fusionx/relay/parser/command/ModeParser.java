package co.fusionx.relay.parser.command;

import com.google.common.base.Optional;

import java.util.List;

import co.fusionx.relay.base.relay.RelayChannel;
import co.fusionx.relay.base.relay.RelayChannelUser;
import co.fusionx.relay.base.relay.RelayMainUser;
import co.fusionx.relay.base.relay.RelayServer;
import co.fusionx.relay.constants.UserLevel;
import co.fusionx.relay.event.channel.ChannelEvent;
import co.fusionx.relay.event.channel.ChannelModeEvent;
import co.fusionx.relay.event.channel.ChannelUserLevelChangeEvent;
import co.fusionx.relay.event.channel.ChannelWorldLevelChangeEvent;
import co.fusionx.relay.function.Optionals;
import co.fusionx.relay.util.IRCUtils;
import co.fusionx.relay.util.LogUtils;

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

            LogUtils.logOptionalBug(optChannel, mServer);
            Optionals.ifPresent(optChannel, channel -> {
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
                    ? new ChannelUserLevelChangeEvent(channel, mode, (RelayMainUser) user,
                    levelEnum,
                    optSending, sendingNick)
                    : new ChannelWorldLevelChangeEvent(channel, mode, user, levelEnum,
                            optSending, sendingNick);
            channel.postAndStoreEvent(event);
        } else {
            final ChannelEvent event = new ChannelModeEvent(channel, optSending, sendingNick,
                    source, mode);
            channel.postAndStoreEvent(event);
        }
    }
}
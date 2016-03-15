package co.fusionx.relay.internal.parser.main.command;

import com.google.common.base.Optional;

import java.util.List;

import co.fusionx.relay.internal.base.RelayChannel;
import co.fusionx.relay.internal.base.RelayChannelUser;
import co.fusionx.relay.internal.base.RelayMainUser;
import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.constants.UserLevel;
import co.fusionx.relay.event.channel.ChannelEvent;
import co.fusionx.relay.event.channel.ChannelModeEvent;
import co.fusionx.relay.event.channel.ChannelUserLevelChangeEvent;
import co.fusionx.relay.event.channel.ChannelWorldLevelChangeEvent;
import co.fusionx.relay.internal.function.Optionals;
import co.fusionx.relay.util.LogUtils;
import co.fusionx.relay.util.ParseUtils;

class ModeParser extends CommandParser {

    public ModeParser(final RelayServer server) {
        super(server);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String prefix) {
        final String sendingUser = ParseUtils.getNickFromPrefix(prefix);
        final String recipient = parsedArray.get(0);
        final String mode = parsedArray.get(1);
        final char firstChar = recipient.charAt(0);

        if (RelayChannel.isChannelPrefix(firstChar)) {
            parseChannelMode(parsedArray, recipient, sendingUser, mode);
        } else {
            // A user is changing a mode about themselves
            // TODO - implement this?
        }
    }

    private void parseChannelMode(final List<String> parsedArray, final String recipient,
            final String sendingUser, final String mode) {
        // The recipient is a channel (i.e. the mode of a user in the channel is being changed
        // or possibly the mode of the channel itself)
        final Optional<RelayChannel> optChannel = mUserChannelInterface.getChannel(recipient);

        LogUtils.logOptionalBug(optChannel, mServer);
        Optionals.ifPresent(optChannel, channel -> {
            // TODO - implement channel mode changes
            onUserModeInChannel(parsedArray, sendingUser, channel, mode);
        });
    }

    private void onUserModeInChannel(final List<String> parsedArray, final String sendingNick,
            final RelayChannel channel, final String mode) {
        final String source = parsedArray.size() > 2 ? parsedArray.get(2) : "";
        final String nick = ParseUtils.getNickFromPrefix(source);
        final boolean appUser = mServer.getUser().isNickEqual(nick);

        final Optional<RelayChannelUser> optUser = appUser
                ? Optional.of(mServer.getUser())
                : mUserChannelInterface.getUser(nick);
        final Optional<RelayChannelUser> optSending = mUserChannelInterface.getUser(sendingNick);

        // Nullity can occur when a ban is being added/removed on a whole range using wildcards
        final ChannelEvent event;
        if (optUser.isPresent()) {
            final RelayChannelUser user = optUser.get();

            final UserLevel oldLevel = user.getChannelPrivileges(channel);
            final UserLevel newLevel = parseChannelUserModeChange(mode);
            user.onModeChanged(channel, newLevel);

            event = appUser
                    ? new ChannelUserLevelChangeEvent(channel, mode, (RelayMainUser) user,
                    oldLevel, newLevel, optSending, sendingNick)
                    : new ChannelWorldLevelChangeEvent(channel, mode, user, oldLevel, newLevel,
                            optSending, sendingNick);
        } else {
            event = new ChannelModeEvent(channel, optSending, sendingNick,
                    source, mode);
        }
        channel.postAndStoreEvent(event);
    }

    private UserLevel parseChannelUserModeChange(final String mode) {
        boolean addingMode = false;
        for (char character : mode.toCharArray()) {
            switch (character) {
                case '+':
                    addingMode = true;
                    break;
                case '-':
                    addingMode = false;
                    break;
                case 'o':
                case 'v':
                case 'h':
                case 'a':
                case 'q':
                    // TODO - don't return straight away - more checking may need to be done
                    final UserLevel levelEnum = UserLevel.getLevelFromMode(character);
                    return addingMode ? levelEnum : UserLevel.NONE;
            }
        }
        return UserLevel.NONE;
    }
}
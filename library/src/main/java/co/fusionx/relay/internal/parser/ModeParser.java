package co.fusionx.relay.internal.parser;

import com.google.common.base.Optional;

import java.util.List;

import co.fusionx.relay.core.ChannelUser;
import co.fusionx.relay.conversation.Server;
import co.fusionx.relay.constants.UserLevel;
import co.fusionx.relay.event.channel.ChannelEvent;
import co.fusionx.relay.event.channel.ChannelModeEvent;
import co.fusionx.relay.event.channel.ChannelUserLevelChangeEvent;
import co.fusionx.relay.event.channel.ChannelWorldLevelChangeEvent;
import co.fusionx.relay.internal.base.RelayChannel;
import co.fusionx.relay.internal.core.InternalChannel;
import co.fusionx.relay.internal.core.InternalChannelUser;
import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.internal.function.Optionals;
import co.fusionx.relay.util.LogUtils;
import co.fusionx.relay.util.ParseUtils;

public class ModeParser extends CommandParser {

    public ModeParser(final InternalServer server,
            final InternalUserChannelGroup ucmanager,
            final InternalQueryUserGroup queryManager) {
        super(server, ucmanager, queryManager);
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
        final Optional<InternalChannel> optChannel = mUserChannelGroup.getChannel(recipient);

        Optionals.run(optChannel, channel -> {
            // TODO - implement channel mode changes
            onUserModeInChannel(parsedArray, sendingUser, channel, mode);
        }, () -> LogUtils.logOptionalBug(mServer.getConfiguration()));
    }

    private void onUserModeInChannel(final List<String> parsedArray, final String sendingNick,
            final InternalChannel channel, final String mode) {
        final String source = parsedArray.get(2);
        final String nick = ParseUtils.getNickFromPrefix(source);
        final boolean appUser = mUserChannelGroup.getUser().isNickEqual(nick);

        final Optional<InternalChannelUser> optUser = appUser
                ? Optional.of(mUserChannelGroup.getUser())
                : mUserChannelGroup.getUser(nick);
        final Optional<? extends ChannelUser> optSending = mUserChannelGroup.getUser(sendingNick);

        // Nullity can occur when a ban is being added/removed on a whole range using wildcards
        final ChannelEvent event;
        if (optUser.isPresent()) {
            final InternalChannelUser user = optUser.get();

            final UserLevel oldLevel = user.getChannelPrivileges(channel);
            final UserLevel newLevel = parseChannelUserModeChange(mode);
            user.onModeChanged(channel, newLevel);

            event = appUser
                    ? new ChannelUserLevelChangeEvent(channel, mode, mUserChannelGroup.getUser(), oldLevel,
                    newLevel, optSending, sendingNick)
                    : new ChannelWorldLevelChangeEvent(channel, mode, user, oldLevel, newLevel,
                            optSending, sendingNick);
        } else {
            event = new ChannelModeEvent(channel, optSending, sendingNick, source, mode);
        }
        channel.postEvent(event);
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
package co.fusionx.relay.internal.parser;

import com.google.common.base.Optional;

import java.util.Collection;
import java.util.List;

import co.fusionx.relay.core.ChannelUser;
import co.fusionx.relay.constant.UserLevel;
import co.fusionx.relay.event.channel.ChannelWorldKickEvent;
import co.fusionx.relay.event.channel.ChannelWorldUserEvent;
import co.fusionx.relay.event.server.KickEvent;
import co.fusionx.relay.internal.core.InternalChannel;
import co.fusionx.relay.internal.core.InternalChannelUser;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.function.Optionals;
import co.fusionx.relay.parser.CommandParser;
import co.fusionx.relay.util.LogUtils;
import co.fusionx.relay.util.ParseUtils;

public class KickParser implements CommandParser {

    protected final InternalServer mServer;

    protected final InternalUserChannelGroup mUserChannelGroup;

    public KickParser(final InternalServer server,
            final InternalUserChannelGroup userChannelGroup) {
        mServer = server;
        mUserChannelGroup = userChannelGroup;
    }

    /**
     * Called when a user who is not our user is kicked from the channel
     *
     * @param parsedArray the raw line which is split
     * @return the WorldUser object associated with the nick
     */
    public Optional<InternalChannelUser> getRemovedUser(final List<String> parsedArray) {
        final String kickedNick = parsedArray.get(3);
        return mUserChannelGroup.getUser(kickedNick);
    }

    public ChannelWorldUserEvent getEvent(final List<String> parsedArray,
            final String rawSource, final InternalChannel channel, final ChannelUser kickedUser) {
        final UserLevel level = kickedUser.getChannelPrivileges(channel);
        final String kickingNick = ParseUtils.getNickFromPrefix(rawSource);
        final Optional<? extends ChannelUser> optKickUser = mUserChannelGroup.getUser(kickingNick);
        final String reason = parsedArray.size() == 5 ? parsedArray.get(4).replace("\"", "") : "";

        return new ChannelWorldKickEvent(channel, kickedUser, level, optKickUser, kickingNick,
                reason);
    }

    /**
     * Method called when the user is kicked from the channel
     *
     * @param parsedArray the raw line which is split
     * @param rawSource   the source of the person who kicked us
     * @param channel     the channel we were kicked from
     */
    void onRemoved(final List<String> parsedArray, final String rawSource,
            final InternalChannel channel) {
        final String kickingNick = ParseUtils.getNickFromPrefix(rawSource);
        final Optional<? extends ChannelUser> optKickUser = mUserChannelGroup.getUser(kickingNick);

        // Remove the channel only after we've finished with it
        final Collection<InternalChannelUser> users = mUserChannelGroup.removeChannel(channel);
        for (final InternalChannelUser user : users) {
            mUserChannelGroup.removeChannelFromUser(channel, user);
        }

        final String reason = parsedArray.size() == 5 ? parsedArray.get(4).replace("\"", "") : "";
        mServer.postEvent(new KickEvent(mServer, channel, optKickUser, kickingNick, reason));
    }

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        final String channelName = parsedArray.get(0);

        final Optional<InternalChannel> optChannel = mUserChannelGroup.getChannel(channelName);
        Optionals.run(optChannel, channel -> {
            final Optional<InternalChannelUser> optUser = getRemovedUser(parsedArray);
            Optionals.run(optUser, user -> {
                if (mUserChannelGroup.getUser().isNickEqual(user)) {
                    onRemoved(parsedArray, prefix, channel);
                } else {
                    onUserRemoved(parsedArray, prefix, channel, user);
                }
            }, () -> LogUtils.logOptionalBug(mServer.getConfiguration()));
        }, () -> LogUtils.logOptionalBug(mServer.getConfiguration()));
    }

    private void onUserRemoved(final List<String> parsedArray, final String rawSource,
            final InternalChannel channel, final InternalChannelUser removedUser) {
        mUserChannelGroup.decoupleUserAndChannel(removedUser, channel);

        channel.postEvent(getEvent(parsedArray, rawSource, channel, removedUser));
    }
}
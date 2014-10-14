package co.fusionx.relay.internal.statechanger.rfc;

import com.google.common.base.Optional;

import java.util.Collection;

import co.fusionx.relay.constant.UserLevel;
import co.fusionx.relay.core.ChannelUser;
import co.fusionx.relay.event.channel.ChannelWorldPartEvent;
import co.fusionx.relay.event.channel.ChannelWorldUserEvent;
import co.fusionx.relay.event.channel.PartEvent;
import co.fusionx.relay.internal.core.InternalChannel;
import co.fusionx.relay.internal.core.InternalChannelUser;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.function.Optionals;
import co.fusionx.relay.parser.rfc.PartParser;
import co.fusionx.relay.internal.util.LogUtils;
import co.fusionx.relay.util.ParseUtils;

public class PartStateChanger implements PartParser.PartObserver {

    private final InternalServer mServer;

    private final InternalUserChannelGroup mUserChannelGroup;

    public PartStateChanger(final InternalServer server,
            final InternalUserChannelGroup userChannelGroup) {
        mServer = server;
        mUserChannelGroup = userChannelGroup;
    }

    @Override
    public void onPart(final String prefix, final String channelName,
            final Optional<String> optionalReason) {
        final Optional<InternalChannel> optChannel = mUserChannelGroup.getChannel(channelName);
        Optionals.run(optChannel, channel -> {
            final Optional<InternalChannelUser> optUser = getRemovedUser(prefix);
            Optionals.run(optUser, user -> {
                if (mUserChannelGroup.getUser().isNickEqual(user)) {
                    onSelfRemoved(channel);
                } else {
                    onUserRemoved(channel, user, optionalReason);
                }
            }, () -> LogUtils.logOptionalBug(mServer.getConfiguration()));
        }, () -> LogUtils.logOptionalBug(mServer.getConfiguration()));
    }

    public Optional<InternalChannelUser> getRemovedUser(final String rawSource) {
        final String userNick = ParseUtils.getNickFromPrefix(rawSource);
        return mUserChannelGroup.getUser(userNick);
    }

    private ChannelWorldUserEvent getEvent(final InternalChannel channel, final ChannelUser user,
            final Optional<String> optionalReason) {
        final UserLevel level = user.getChannelPrivileges(channel);
        return new ChannelWorldPartEvent(channel, user, level, optionalReason);
    }

    private void onSelfRemoved(final InternalChannel channel) {
        // ZNCs can be stupid and can sometimes send PART commands for channels they didn't send
        // JOIN commands for...
        if (channel == null) {
            return;
        }

        final Collection<InternalChannelUser> users = mUserChannelGroup.removeChannel(channel);
        for (final InternalChannelUser user : users) {
            mUserChannelGroup.removeChannelFromUser(channel, user);
        }
        channel.postEvent(new PartEvent(channel));
    }

    private void onUserRemoved(final InternalChannel channel, final InternalChannelUser
            removedUser, final Optional<String> optionalReason) {
        mUserChannelGroup.decoupleUserAndChannel(removedUser, channel);

        final ChannelWorldUserEvent event = getEvent(channel, removedUser, optionalReason);
        channel.postEvent(event);
    }
}
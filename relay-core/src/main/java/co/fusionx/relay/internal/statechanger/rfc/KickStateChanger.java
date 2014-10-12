package co.fusionx.relay.internal.statechanger.rfc;

import com.google.common.base.Optional;

import java.util.Collection;

import co.fusionx.relay.constant.UserLevel;
import co.fusionx.relay.core.ChannelUser;
import co.fusionx.relay.event.channel.ChannelEvent;
import co.fusionx.relay.event.channel.ChannelWorldKickEvent;
import co.fusionx.relay.event.server.KickEvent;
import co.fusionx.relay.function.Optionals;
import co.fusionx.relay.internal.core.InternalChannel;
import co.fusionx.relay.internal.core.InternalChannelUser;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.parser.rfc.KickParser;
import co.fusionx.relay.util.LogUtils;
import co.fusionx.relay.util.ParseUtils;

public class KickStateChanger implements KickParser.KickObserver {

    protected final InternalServer mServer;

    protected final InternalUserChannelGroup mUserChannelGroup;

    public KickStateChanger(final InternalServer server,
            final InternalUserChannelGroup userChannelGroup) {
        mServer = server;
        mUserChannelGroup = userChannelGroup;
    }

    @Override
    public void onKick(final String prefix, final String channelName, final String kickedNick,
            final Optional<String> optionalReason) {
        final Optional<InternalChannel> optChannel = mUserChannelGroup.getChannel(channelName);
        Optionals.run(optChannel, channel -> {
            final Optional<InternalChannelUser> optKicked = mUserChannelGroup.getUser(kickedNick);
            Optionals.run(optKicked, user -> {
                if (mUserChannelGroup.getUser().isNickEqual(user)) {
                    onLibraryRemoved(prefix, optionalReason, channel);
                } else {
                    onUserRemoved(prefix, optionalReason, channel, user);
                }
            }, () -> LogUtils.logOptionalBug(mServer.getConfiguration()));
        }, () -> LogUtils.logOptionalBug(mServer.getConfiguration()));
    }

    private void onLibraryRemoved(final String prefix, final Optional<String> reason,
            final InternalChannel channel) {
        final String kickingNick = ParseUtils.getNickFromPrefix(prefix);
        final Optional<? extends ChannelUser> optKickUser = mUserChannelGroup.getUser(kickingNick);

        // Remove the channel only after we've finished with it
        final Collection<InternalChannelUser> users = mUserChannelGroup.removeChannel(channel);
        for (final InternalChannelUser user : users) {
            mUserChannelGroup.removeChannelFromUser(channel, user);
        }

        mServer.postEvent(new KickEvent(mServer, channel, optKickUser, kickingNick, reason));
    }

    private void onUserRemoved(final String prefix, final Optional<String> reason,
            final InternalChannel channel, final InternalChannelUser kickedUser) {
        mUserChannelGroup.decoupleUserAndChannel(kickedUser, channel);

        final UserLevel level = kickedUser.getChannelPrivileges(channel);
        final String kickingNick = ParseUtils.getNickFromPrefix(prefix);
        final Optional<? extends ChannelUser> optKickUser = mUserChannelGroup.getUser(kickingNick);

        final ChannelEvent event = new ChannelWorldKickEvent(channel, kickedUser, level,
                optKickUser, kickingNick, reason);
        channel.postEvent(event);
    }
}
package co.fusionx.relay.internal.parser.main.command;

import com.google.common.base.Optional;

import java.util.Collection;
import java.util.List;

import co.fusionx.relay.core.ChannelUser;
import co.fusionx.relay.conversation.Server;
import co.fusionx.relay.constants.UserLevel;
import co.fusionx.relay.event.channel.ChannelWorldKickEvent;
import co.fusionx.relay.event.channel.ChannelWorldUserEvent;
import co.fusionx.relay.event.server.KickEvent;
import co.fusionx.relay.internal.base.RelayChannel;
import co.fusionx.relay.internal.core.InternalChannel;
import co.fusionx.relay.internal.core.InternalChannelUser;
import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.util.ParseUtils;

public class KickParser extends RemoveUserParser {

    public KickParser(final Server server,
            final InternalUserChannelGroup ucmanager,
            final InternalQueryUserGroup queryManager) {
        super(server, ucmanager, queryManager);
    }

    /**
     * Called when a user who is not our user is kicked from the channel
     *
     * @param parsedArray the raw line which is split
     * @param rawSource   the the source of the person who kicked the other user - unused in this
     *                    method
     * @return the WorldUser object associated with the nick
     */
    @Override
    public Optional<InternalChannelUser> getRemovedUser(final List<String> parsedArray,
            final String rawSource) {
        final String kickedNick = parsedArray.get(3);
        return mUserChannelGroup.getUser(kickedNick);
    }

    @Override
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
    @Override
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
        mServer.getBus().post(new KickEvent(mServer, channel, optKickUser, kickingNick, reason));
    }
}
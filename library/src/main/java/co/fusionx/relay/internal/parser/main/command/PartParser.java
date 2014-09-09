package co.fusionx.relay.internal.parser.main.command;

import com.google.common.base.Optional;

import java.util.Collection;
import java.util.List;

import co.fusionx.relay.core.ChannelUser;
import co.fusionx.relay.internal.base.RelayChannel;
import co.fusionx.relay.constants.UserLevel;
import co.fusionx.relay.event.channel.ChannelWorldPartEvent;
import co.fusionx.relay.event.channel.ChannelWorldUserEvent;
import co.fusionx.relay.event.channel.PartEvent;
import co.fusionx.relay.internal.core.InternalChannel;
import co.fusionx.relay.internal.core.InternalChannelUser;
import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.util.ParseUtils;

public class PartParser extends RemoveUserParser {

    public PartParser(final InternalServer server,
            final InternalUserChannelGroup ucmanager,
            final InternalQueryUserGroup queryManager) {
        super(server, ucmanager, queryManager);
    }

    @Override
    public Optional<InternalChannelUser> getRemovedUser(final List<String> parsedArray,
            final String rawSource) {
        final String userNick = ParseUtils.getNickFromPrefix(rawSource);
        return mUserChannelGroup.getUser(userNick);
    }

    @Override
    public ChannelWorldUserEvent getEvent(final List<String> parsedArray, final String rawSource,
            final InternalChannel channel, final ChannelUser user) {
        final UserLevel level = user.getChannelPrivileges(channel);
        final String reason = parsedArray.size() == 2 ? parsedArray.get(1).replace("\"", "") : "";
        return new ChannelWorldPartEvent(channel, user, level, reason);
    }

    @Override
    void onRemoved(final List<String> parsedArray, final String rawSource,
            final InternalChannel channel) {
        // ZNCs can be stupid and can sometimes send PART commands for channels they didn't send
        // JOIN commands for...
        if (channel == null) {
            return;
        }

        final Collection<InternalChannelUser> users = mUserChannelGroup.removeChannel(channel);
        for (final InternalChannelUser user : users) {
            mUserChannelGroup.removeChannelFromUser(channel, user);
        }
        channel.getBus().post(new PartEvent(channel));
    }
}
package co.fusionx.relay.internal.parser.main.command;

import com.google.common.base.Optional;

import java.util.List;

import co.fusionx.relay.constants.CapCapability;
import co.fusionx.relay.event.channel.ChannelEvent;
import co.fusionx.relay.event.channel.ChannelWorldJoinEvent;
import co.fusionx.relay.event.server.JoinEvent;
import co.fusionx.relay.event.server.ServerEvent;
import co.fusionx.relay.internal.core.InternalChannel;
import co.fusionx.relay.internal.core.InternalChannelUser;
import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;

public class JoinParser extends CommandParser {

    public JoinParser(final InternalServer server,
            final InternalUserChannelGroup ucmanager,
            final InternalQueryUserGroup queryManager) {
        super(server, ucmanager, queryManager);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String prefix) {
        final String channelName = parsedArray.get(0);

        // Retrieve the user and channel
        final InternalChannelUser user = mUserChannelGroup.getUserFromPrefix(prefix);
        final Optional<InternalChannel> optChannel = mUserChannelGroup.getChannel(channelName);
        InternalChannel channel = optChannel.orNull();

        // Store whether the user is the app user
        final boolean appUser = mUserChannelGroup.getUser().isNickEqual(user);
        if (channel == null) {
            // If the channel is null then we haven't joined it before (disconnection) and we should
            // create a new channel
            channel = mUserChannelGroup.getNewChannel(channelName);
        } else if (appUser) {
            // If the channel is not null then we simply clear the data of the channel
            channel.clearInternalData();
        }
        // Put the user and channel together
        mUserChannelGroup.coupleUserAndChannel(user, channel);

        if (mServer.getCapabilities().contains(CapCapability.EXTENDEDJOIN)) {
            // We should have 2 parameters after the channel name
            if (parsedArray.size() == 3) {
                final String accountName = parsedArray.get(1);
                final String realName = parsedArray.get(2);
            } else {
                // TODO - this should never happen - the server is messing up if this is the case
            }
        }

        // Post the event to the channel
        final ChannelEvent event = new ChannelWorldJoinEvent(channel, user);
        channel.getBus().post(event);

        if (appUser) {
            // Also post a server event if the user who joined was the app user
            final ServerEvent joinEvent = new JoinEvent(mServer, channel);
            mServer.getBus().post(joinEvent);
        }
    }
}
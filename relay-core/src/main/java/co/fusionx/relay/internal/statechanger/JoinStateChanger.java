package co.fusionx.relay.internal.statechanger;

import com.google.common.base.Optional;

import java.util.List;

import javax.inject.Inject;

import co.fusionx.relay.event.channel.ChannelEvent;
import co.fusionx.relay.event.channel.ChannelWorldJoinEvent;
import co.fusionx.relay.event.server.JoinEvent;
import co.fusionx.relay.event.server.ServerEvent;
import co.fusionx.relay.internal.core.InternalChannel;
import co.fusionx.relay.internal.core.InternalChannelUser;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.parser.CommandParser;
import co.fusionx.relay.parser.rfc.JoinParser;

public class JoinStateChanger implements JoinParser.JoinObserver, CommandParser {

    private final InternalServer mInternalServer;

    private final InternalUserChannelGroup mUserChannelGroup;

    private final CommandParser mJoinParser;

    @Inject
    public JoinStateChanger(final InternalServer internalServer,
            final InternalUserChannelGroup userChannelGroup) {
        mInternalServer = internalServer;
        mUserChannelGroup = userChannelGroup;

        // This is intentionally not injected since JoinParser is so straightforward and adds
        // more boilerplate than should be needed
        mJoinParser = new JoinParser(this);
    }

    @Override
    public void onJoin(final String prefix, final String channelName) {
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
            channel.reset();
        }
        // Put the user and channel together
        mUserChannelGroup.coupleUserAndChannel(user, channel);

        // Post the event to the channel
        final ChannelEvent event = new ChannelWorldJoinEvent(channel, user);
        channel.postEvent(event);

        if (appUser) {
            // Also post a server event if the user who joined was the app user
            final ServerEvent joinEvent = new JoinEvent(mInternalServer, channel);
            mInternalServer.postEvent(joinEvent);
        }
    }

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        mJoinParser.parseCommand(parsedArray, prefix);
    }

}
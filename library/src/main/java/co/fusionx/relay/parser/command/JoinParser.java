package co.fusionx.relay.parser.command;

import com.google.common.base.Optional;

import co.fusionx.relay.RelayChannel;
import co.fusionx.relay.RelayChannelUser;
import co.fusionx.relay.RelayServer;
import co.fusionx.relay.event.channel.ChannelEvent;
import co.fusionx.relay.event.channel.ChannelWorldJoinEvent;
import co.fusionx.relay.event.server.JoinEvent;
import co.fusionx.relay.event.server.ServerEvent;

import java.util.List;

class JoinParser extends CommandParser {

    private static final int CHANNEL_NAME_INDEX = 2;

    public JoinParser(final RelayServer server) {
        super(server);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String rawSource) {
        final String channelName = parsedArray.get(CHANNEL_NAME_INDEX);

        // Retrieve the user and channel
        final RelayChannelUser user = mUserChannelInterface.getUserFromRaw(rawSource);
        final Optional<RelayChannel> optChannel = mUserChannelInterface.getChannel(channelName);
        RelayChannel channel = optChannel.orNull();

        // Store whether the user is the app user
        final boolean appUser = mServer.getUser().isNickEqual(user);
        if (!optChannel.isPresent()) {
            // If the channel is null then we haven't joined it before (disconnection) and we should
            // create a new channel
            channel = mUserChannelInterface.getNewChannel(channelName);
        } else if (appUser) {
            // If the channel is not null then we simply clear the data of the channel
            channel.clearInternalData();
        }
        // Put the user and channel together
        mUserChannelInterface.coupleUserAndChannel(user, channel);

        // Post the event to the channel
        final ChannelEvent event = new ChannelWorldJoinEvent(channel, user);
        mServerEventBus.postAndStoreEvent(event, channel);

        if (!appUser) {
            return;
        }
        // Also post a server event if the user who joined was the app user
        final ServerEvent joinEvent = new JoinEvent(channel);
        mServerEventBus.postAndStoreEvent(joinEvent);
    }
}
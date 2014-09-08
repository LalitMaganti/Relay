package co.fusionx.relay.internal.parser.main.command;

import com.google.common.base.Optional;

import java.util.List;

import co.fusionx.relay.constants.CapCapability;
import co.fusionx.relay.event.channel.ChannelEvent;
import co.fusionx.relay.event.channel.ChannelWorldJoinEvent;
import co.fusionx.relay.event.server.JoinEvent;
import co.fusionx.relay.event.server.ServerEvent;
import co.fusionx.relay.internal.base.RelayChannel;
import co.fusionx.relay.internal.base.RelayChannelUser;
import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.internal.base.RelayUserChannelDao;

public class JoinParser extends CommandParser {

    public JoinParser(final RelayServer server,
            final RelayUserChannelDao userChannelInterface) {
        super(server, userChannelInterface);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String prefix) {
        final String channelName = parsedArray.get(0);

        // Retrieve the user and channel
        final RelayChannelUser user = mDao.getUserFromPrefix(prefix);
        final Optional<RelayChannel> optChannel = mDao.getChannel(channelName);
        RelayChannel channel = optChannel.orNull();

        // Store whether the user is the app user
        final boolean appUser = mUser.isNickEqual(user);
        if (channel == null) {
            // If the channel is null then we haven't joined it before (disconnection) and we should
            // create a new channel
            channel = mDao.getNewChannel(channelName);
        } else if (appUser) {
            // If the channel is not null then we simply clear the data of the channel
            channel.clearInternalData();
        }
        // Put the user and channel together
        mDao.coupleUserAndChannel(user, channel);

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
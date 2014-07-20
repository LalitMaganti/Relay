package com.fusionx.relay.parser.command;

import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.RelayChannel;
import com.fusionx.relay.RelayChannelUser;
import com.fusionx.relay.RelayServer;
import com.fusionx.relay.event.channel.ChannelWorldKickEvent;
import com.fusionx.relay.event.channel.ChannelWorldUserEvent;
import com.fusionx.relay.event.server.KickEvent;
import com.fusionx.relay.util.IRCUtils;

import java.util.Collection;
import java.util.List;

class KickParser extends RemoveUserParser {

    public KickParser(RelayServer server) {
        super(server);
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
    public RelayChannelUser getRemovedUser(final List<String> parsedArray, final String rawSource) {
        final String kickedNick = parsedArray.get(3);
        return getUserChannelInterface().getUser(kickedNick);
    }

    @Override
    public ChannelWorldUserEvent getEvent(final List<String> parsedArray, final String rawSource,
            final RelayChannel channel, final ChannelUser kickedUser) {
        final String kickingNick = IRCUtils.getNickFromRaw(rawSource);
        final ChannelUser kickingUser = getUserChannelInterface().getUser(kickingNick);
        final String reason = parsedArray.size() == 5 ? parsedArray.get(4).replace("\"", "") : "";

        return new ChannelWorldKickEvent(channel, kickedUser, kickingUser, kickingNick, reason);
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
            final RelayChannel channel) {
        final String kickingNick = IRCUtils.getNickFromRaw(rawSource);
        final ChannelUser kickingUser = getUserChannelInterface().getUser(kickingNick);

        // Remove the channel only after we've finished with it
        final Collection<RelayChannelUser> users = getUserChannelInterface().removeChannel(channel);
        for (final RelayChannelUser user : users) {
            getUserChannelInterface().removeChannelFromUser(channel, user);
        }

        final String reason = parsedArray.size() == 5 ? parsedArray.get(4).replace("\"", "") : "";
        final KickEvent event = new KickEvent(channel, kickingUser, reason);

        getServerEventBus().postAndStoreEvent(event);
    }
}
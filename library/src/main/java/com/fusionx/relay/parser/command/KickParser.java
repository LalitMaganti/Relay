package com.fusionx.relay.parser.command;

import com.fusionx.relay.Channel;
import com.fusionx.relay.Server;
import com.fusionx.relay.WorldUser;
import com.fusionx.relay.event.channel.WorldKickEvent;
import com.fusionx.relay.event.channel.WorldUserEvent;
import com.fusionx.relay.event.server.KickEvent;
import com.fusionx.relay.util.IRCUtils;

import java.util.List;

class KickParser extends RemoveUserParser {

    public KickParser(Server server) {
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
    public WorldUser getRemovedUser(final List<String> parsedArray, final String rawSource) {
        final String kickedNick = parsedArray.get(3);
        return getUserChannelInterface().getUserIfExists(kickedNick);
    }

    @Override
    public WorldUserEvent getEvent(final List<String> parsedArray, final String rawSource,
            final Channel channel, final WorldUser kickedUser) {
        final String kickingNick = IRCUtils.getNickFromRaw(rawSource);
        final WorldUser kickingUser = getUserChannelInterface().getUserIfExists(kickingNick);
        final String reason = parsedArray.size() == 5 ? parsedArray.get(4).replace("\"", "") : "";

        return new WorldKickEvent(channel, kickedUser, kickingUser, kickingNick, reason);
    }

    /**
     * Method called when the user is kicked from the channel
     *
     * @param parsedArray the raw line which is split
     * @param rawSource   the source of the person who kicked us
     * @param channel     the channel we were kicked from
     */
    @Override
    void onRemoved(final List<String> parsedArray, final String rawSource, final Channel channel) {
        final String kickingNick = IRCUtils.getNickFromRaw(rawSource);
        final WorldUser kickingUser = getUserChannelInterface().getUserIfExists(kickingNick);

        // Remove the channel only after we've finished with it
        getUserChannelInterface().removeChannel(channel);

        final String reason = parsedArray.size() == 5 ? parsedArray.get(4).replace("\"", "") : "";
        final KickEvent event = new KickEvent(channel, kickingUser, reason);

        getServerEventBus().postAndStoreEvent(event);
    }
}
package com.fusionx.relay.parser.command;

import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.util.IRCUtils;

import java.util.List;

public class KickParser extends RemoveUserParser {

    public KickParser(Server server) {
        super(server);
    }

    /**
     * Called when a user who is not our user is kicked from the channel
     *
     * @param parsedArray the raw line which is split
     * @param rawSource the the source of the person who kicked the other user - unused in this
     *                  method
     * @return the ChannelUser object associated with the nick
     */
    @Override
    public ChannelUser getRemovedUser(final List<String> parsedArray, final String rawSource) {
        final String kickedNick = parsedArray.get(3);
        return mUserChannelInterface.getUser(kickedNick);
    }

    /**
     * The message to broadcast when a user other than our user is kicked from a channel
     *
     * @param parsedArray the raw line which is split
     * @param channel the channel that the user was kicked from
     * @param user the user that was kicked
     * @return the message to broadcast
     */
    @Override
    public String getUserRemoveMessage(List<String> parsedArray, Channel channel, ChannelUser user) {
        final String kickedNick = parsedArray.get(3);
        final ChannelUser kickedUser = mUserChannelInterface.getUser(kickedNick);

        final String kickingUserNick = user.getPrettyNick(channel);

        final String reason = parsedArray.size() == 5 ? parsedArray.get(4).replace("\"", "") : "";

        return mEventResponses.getUserKickedMessage(kickedUser.getPrettyNick(channel),
                kickingUserNick, reason);
    }

    /**
     * Method called when the user is kicked from the channel
     *
     * @param parsedArray the raw line which is split
     * @param rawSource the source of the person who kicked us
     * @param channel the channel we were kicked from
     */
    @Override
    void onRemoved(final List<String> parsedArray, final String rawSource, final Channel channel) {
        mUserChannelInterface.removeChannel(channel);

        final String kickingNick = IRCUtils.getNickFromRaw(rawSource);
        final ChannelUser kickingUser = mUserChannelInterface.getUserIfExists(kickingNick);
        final String kickingUserNick = kickingUser.getPrettyNick(channel);

        final String reason = parsedArray.size() == 5 ? parsedArray.get(4).replace("\"", "") : "";
        final String message = mEventResponses.getOnUserKickedMessage(channel.getName(),
                kickingUserNick, reason);
        mServerEventBus.sendGenericServerEvent(mServer, message);
        mServerEventBus.onKicked(channel.getName(), reason);
    }
}
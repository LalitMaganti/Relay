package com.fusionx.relay.parser.command;

import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.util.IRCUtils;

import java.util.List;

public class PartParser extends RemoveUserParser {

    public PartParser(Server server) {
        super(server);
    }

    @Override
    public ChannelUser getRemovedUser(List<String> parsedArray, String rawSource) {
        final String userNick = IRCUtils.getNickFromRaw(rawSource);
        return mUserChannelInterface.getUserIfExists(userNick);
    }

    @Override
    public String getUserRemoveMessage(final List<String> parsedArray, final Channel channel,
            final ChannelUser user) {
        final String reason = parsedArray.size() == 4 ? parsedArray.get(3).replace("\"",
                "") : "";
        return mEventResponses.getPartMessage(user.getPrettyNick(channel), reason);
    }

    @Override
    void onRemoved(final List<String> parsedArray, final String rawSource, final Channel channel) {
        mUserChannelInterface.removeChannel(channel);
        mServerEventBus.onChannelParted(channel.getName());
    }
}
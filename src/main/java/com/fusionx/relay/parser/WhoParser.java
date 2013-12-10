package com.fusionx.relay.parser;

import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.UserChannelInterface;
import com.fusionx.relay.event.Event;

import java.util.ArrayList;

class WhoParser {

    private final UserChannelInterface mUserChannelInterface;

    private Channel mWhoChannel;

    private final Server mServer;

    WhoParser(UserChannelInterface userChannelInterface, final Server server) {
        mUserChannelInterface = userChannelInterface;
        mServer = server;
    }

    Event parseWhoReply(final ArrayList<String> parsedArray) {
        if (mWhoChannel == null) {
            mWhoChannel = mUserChannelInterface.getChannel(parsedArray.get(0));
        }
        final ChannelUser user = mUserChannelInterface.getUser(parsedArray.get(4));
        user.onWhoMode(parsedArray.get(5), mWhoChannel);
        return new Event(user.getNick());
    }

    Event parseWhoFinished() {
        if (mWhoChannel != null && mWhoChannel.getUsers() != null) {
            final Event event = mServer.getServerSenderBus().sendGenericChannelEvent
                    (mWhoChannel, "", true);
            mWhoChannel = null;
            return event;
        } else {
            return new Event("null");
        }
    }
}

package com.fusionx.relay.parser;

import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.UserChannelInterface;
import com.fusionx.relay.constants.UserLevelEnum;
import com.fusionx.relay.event.Event;
import com.fusionx.relay.util.IRCUtils;

import java.util.ArrayList;

class NameParser {

    private final UserChannelInterface mUserChannelInterface;

    private final Server mServer;

    private Channel mChannel;

    NameParser(UserChannelInterface userChannelInterface, final Server server) {
        mUserChannelInterface = userChannelInterface;
        mServer = server;
    }

    Event parseNameReply(final ArrayList<String> parsedArray) {
        if (mChannel == null) {
            mChannel = mUserChannelInterface.getChannel(parsedArray.get(1));
        }
        final ArrayList<String> listOfUsers = IRCUtils.splitRawLine(parsedArray.get(2), false);
        for (final String rawNick : listOfUsers) {
            final ChannelUser user = getNickFromNameReply(rawNick);
            mUserChannelInterface.addChannelToUser(user, mChannel);
            mChannel.getUsers().markForAddition(user);
        }
        return new Event("Test");
    }

    Event parseNameFinished() {
        mChannel.getUsers().addMarked();
        final Event event = mServer.getServerEventBus().sendGenericChannelEvent(mChannel,
                "", true);
        mChannel = null;
        return event;
    }

    ChannelUser getNickFromNameReply(final String rawNameNick) {
        final char firstChar = rawNameNick.charAt(0);
        final UserLevelEnum mode = UserLevelEnum.getLevelFromPrefix(firstChar);
        final ChannelUser user = mUserChannelInterface.getUser(mode == UserLevelEnum.NONE ?
                rawNameNick : rawNameNick.substring(1));
        user.putMode(mChannel, mode);
        mChannel.onIncrementUserType(mode);
        return user;
    }
}
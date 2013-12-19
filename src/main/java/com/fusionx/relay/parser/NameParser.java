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
            final ChannelUser user = getUserFromNameReply(rawNick);
            mUserChannelInterface.addChannelToUser(user, mChannel);
            mChannel.getUsers().markForAddition(user);
        }
        return new Event("Test");
    }

    Event parseNameFinished() {
        mChannel.getUsers().addMarked();
        final Event event = mServer.getServerEventBus().onNameFinished(mChannel,
                mChannel.getUsers());
        mChannel = null;
        return event;
    }

    private ChannelUser getUserFromNameReply(final String rawNameNick) {
        final char firstChar = rawNameNick.charAt(0);
        final UserLevelEnum level = UserLevelEnum.getLevelFromPrefix(firstChar);
        final String nick = level == UserLevelEnum.NONE ? rawNameNick : rawNameNick.substring(1);
        final ChannelUser user = mUserChannelInterface.getUser(nick);

        user.onPutMode(mChannel, level);
        mChannel.onIncrementUserType(level);

        return user;
    }
}
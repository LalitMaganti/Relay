/*
    HoloIRC - an IRC client for Android

    Copyright 2013 Lalit Maganti

    This file is part of HoloIRC.

    HoloIRC is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    HoloIRC is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with HoloIRC. If not, see <http://www.gnu.org/licenses/>.
 */

package com.fusionx.androidirclibrary.parser;

import com.fusionx.androidirclibrary.Channel;
import com.fusionx.androidirclibrary.ChannelUser;
import com.fusionx.androidirclibrary.UserChannelInterface;
import com.fusionx.androidirclibrary.communication.MessageSender;
import com.fusionx.androidirclibrary.constants.UserLevelEnum;
import com.fusionx.androidirclibrary.event.Event;
import com.fusionx.androidirclibrary.util.IRCUtils;

import java.util.ArrayList;

class NameParser {

    private final UserChannelInterface mUserChannelInterface;

    private Channel mChannel;

    private final String mServerTitle;

    NameParser(UserChannelInterface userChannelInterface, final String serverTitle) {
        mUserChannelInterface = userChannelInterface;
        mServerTitle = serverTitle;
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
        final MessageSender sender = MessageSender.getSender(mServerTitle);
        final Event event = sender.sendGenericChannelEvent(mChannel, "", true);
        mChannel = null;
        return event;
    }

    ChannelUser getNickFromNameReply(final String rawNameNick) {
        final char firstChar = rawNameNick.charAt(0);
        final UserLevelEnum mode = UserLevelEnum.getLevelFromPrefix(firstChar);
        mChannel.onIncrementUserType(mode);
        final ChannelUser user = mUserChannelInterface.getUser(mode == UserLevelEnum.NONE ?
                rawNameNick : rawNameNick.substring(1));
        user.putMode(mChannel, mode);
        return user;
    }
}
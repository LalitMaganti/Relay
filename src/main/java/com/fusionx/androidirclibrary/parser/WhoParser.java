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
import com.fusionx.androidirclibrary.Server;
import com.fusionx.androidirclibrary.UserChannelInterface;
import com.fusionx.androidirclibrary.event.Event;

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

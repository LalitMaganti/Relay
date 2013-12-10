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

package com.fusionx.androidirclibrary.misc;

import com.fusionx.androidirclibrary.Channel;
import com.fusionx.androidirclibrary.ChannelUser;
import com.fusionx.androidirclibrary.constants.UserLevelEnum;

import java.util.Comparator;

public class IRCUserComparator implements Comparator<ChannelUser> {

    private final Channel channel;

    public IRCUserComparator(final Channel channel) {
        this.channel = channel;
    }

    @Override
    public int compare(final ChannelUser user1, final ChannelUser user2) {
        final UserLevelEnum firstUserMode = user1.getChannelPrivileges(channel);
        final UserLevelEnum secondUserMode = user2.getChannelPrivileges(channel);

        /**
         * Code for compatibility with objects being removed
         */
        if (firstUserMode == null && secondUserMode == null) {
            return 0;
        } else if (firstUserMode == null) {
            return -1;
        } else if (secondUserMode == null) {
            return 1;
        }

        if (firstUserMode.equals(secondUserMode)) {
            final String firstRemoved = user1.getNick();
            final String secondRemoved = user2.getNick();

            return firstRemoved.compareToIgnoreCase(secondRemoved);
        } else if (firstUserMode.ordinal() > secondUserMode.ordinal()) {
            return -1;
        } else {
            return 1;
        }
    }
}
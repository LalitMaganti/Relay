package com.fusionx.relay.misc;

import com.fusionx.relay.Channel;
import com.fusionx.relay.WorldUser;
import com.fusionx.relay.constants.UserLevelEnum;

import java.util.Comparator;

public class IRCUserComparator implements Comparator<WorldUser> {

    private final Channel channel;

    public IRCUserComparator(final Channel channel) {
        this.channel = channel;
    }

    @Override
    public int compare(final WorldUser user1, final WorldUser user2) {
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
            return 1;
        } else {
            return -1;
        }
    }
}
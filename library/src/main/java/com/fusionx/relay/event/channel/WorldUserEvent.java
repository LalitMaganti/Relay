package com.fusionx.relay.event.channel;

import com.google.common.collect.ImmutableList;

import com.fusionx.relay.Channel;
import com.fusionx.relay.WorldUser;

import java.util.List;

public abstract class WorldUserEvent extends ChannelEvent {

    public static final List sUserListChangeEvents = ImmutableList
            .of(WorldJoinEvent.class, WorldKickEvent.class, WorldLevelChangeEvent.class,
                    WorldNickChangeEvent.class, WorldPartEvent.class, WorldQuitEvent.class);

    public final boolean userMentioned;

    public final String nick;

    WorldUserEvent(final Channel channel, final WorldUser user) {
        this(channel, user != null ? user.getPrettyNick(channel) : null);
    }

    WorldUserEvent(final Channel channel, final String nick) {
        this(channel, nick, false);
    }

    WorldUserEvent(final Channel channel, final String nick, final boolean userMentioned) {
        super(channel);
        this.userMentioned = userMentioned;

        // NICK should never be null
        if (nick != null) {
            this.nick = nick;
        } else {
            throw new NullPointerException();
        }
    }
}
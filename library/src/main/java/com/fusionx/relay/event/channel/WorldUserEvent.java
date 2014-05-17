package com.fusionx.relay.event.channel;

import com.google.common.collect.ImmutableList;

import com.fusionx.relay.Channel;
import com.fusionx.relay.WorldUser;
import com.fusionx.relay.nick.Nick;

import java.util.List;

public abstract class WorldUserEvent extends ChannelEvent {

    public static final List<? extends Class<? extends ChannelEvent>> sUserListChangeEvents =
            ImmutableList.of(WorldJoinEvent.class, WorldKickEvent.class,
                    WorldLevelChangeEvent.class, WorldNickChangeEvent.class,
                    WorldPartEvent.class, WorldQuitEvent.class);

    public final boolean userMentioned;

    public final WorldUser user;

    public final Nick userNick;

    public final String userNickString;

    WorldUserEvent(final Channel channel, final WorldUser user) {
        this(channel, user, false);
    }

    WorldUserEvent(final Channel channel, final WorldUser user, final boolean mentioned) {
        super(channel);

        this.user = user;
        this.userNick = user.getNick();
        this.userMentioned = mentioned;
        this.userNickString = user.getNick().getNickAsString();
    }

    WorldUserEvent(final Channel channel, final Nick nick) {
        super(channel);

        this.user = null;
        this.userNick = nick;
        this.userMentioned = false;
        this.userNickString = nick.getNickAsString();
    }

    public WorldUserEvent(final Channel channel, final String nick, final boolean mention) {
        super(channel);

        this.user = null;
        this.userNick = null;
        this.userMentioned = mention;
        this.userNickString = nick;
    }

    public boolean isUserListChangeEvent() {
        return sUserListChangeEvents.contains(getClass());
    }
}
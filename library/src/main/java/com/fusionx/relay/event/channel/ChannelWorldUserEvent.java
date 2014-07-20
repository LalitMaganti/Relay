package com.fusionx.relay.event.channel;

import com.google.common.collect.ImmutableList;

import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.Nick;

import java.util.List;

public abstract class ChannelWorldUserEvent extends ChannelEvent {

    public static final List<? extends Class<? extends ChannelEvent>> sUserListChangeEvents =
            ImmutableList.of(ChannelWorldJoinEvent.class, ChannelWorldKickEvent.class,
                    ChannelWorldLevelChangeEvent.class, ChannelWorldNickChangeEvent.class,
                    ChannelWorldPartEvent.class, ChannelWorldQuitEvent.class);

    public final boolean userMentioned;

    public final ChannelUser user;

    public final Nick userNick;

    public final String userNickString;

    ChannelWorldUserEvent(final Channel channel, final ChannelUser user) {
        this(channel, user, false);
    }

    ChannelWorldUserEvent(final Channel channel, final ChannelUser user, final boolean mentioned) {
        super(channel);

        this.user = user;
        this.userNick = user.getNick();
        this.userMentioned = mentioned;
        this.userNickString = user.getNick().getNickAsString();
    }

    ChannelWorldUserEvent(final Channel channel, final Nick nick) {
        super(channel);

        this.user = null;
        this.userNick = nick;
        this.userMentioned = false;
        this.userNickString = nick.getNickAsString();
    }

    public ChannelWorldUserEvent(final Channel channel, final String nick, final boolean mention) {
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
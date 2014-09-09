package co.fusionx.relay.event.channel;

import com.google.common.collect.ImmutableList;

import java.util.List;

import co.fusionx.relay.conversation.Channel;
import co.fusionx.relay.core.ChannelUser;
import co.fusionx.relay.core.Nick;

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

    ChannelWorldUserEvent(final Channel channel, final String nick, final boolean mention) {
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
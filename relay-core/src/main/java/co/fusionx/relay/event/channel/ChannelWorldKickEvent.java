package co.fusionx.relay.event.channel;

import com.google.common.base.Optional;

import co.fusionx.relay.conversation.Channel;
import co.fusionx.relay.core.ChannelUser;
import co.fusionx.relay.core.Nick;
import co.fusionx.relay.constant.UserLevel;

public class ChannelWorldKickEvent extends ChannelWorldUserEvent {

    public final UserLevel level;

    public final Optional<? extends ChannelUser> kickingUser;

    public final String kickingNickString;

    public final Optional<String> reason;

    public ChannelWorldKickEvent(final Channel channel, final ChannelUser kickedUser,
            final UserLevel level, final Optional<? extends ChannelUser> optKickingUser,
            final String kickingNickString, final Optional<String> reason) {
        super(channel, kickedUser);

        this.level = level;
        this.kickingUser = optKickingUser;
        this.kickingNickString = kickingNickString;
        this.reason = reason;
    }
}
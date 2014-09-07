package co.fusionx.relay.event.channel;

import com.google.common.base.Optional;

import co.fusionx.relay.base.Channel;
import co.fusionx.relay.base.ChannelUser;
import co.fusionx.relay.base.Nick;
import co.fusionx.relay.internal.base.RelayChannelUser;
import co.fusionx.relay.constants.UserLevel;

public class ChannelWorldKickEvent extends ChannelWorldUserEvent {

    public final UserLevel level;

    public final Nick kickingNick;

    public final String kickingNickString;

    public final String reason;

    public ChannelWorldKickEvent(final Channel channel, final ChannelUser kickedUser,
            final UserLevel level, final Optional<RelayChannelUser> optKickingUser,
            final String kickingNickString, final String reason) {
        super(channel, kickedUser);

        this.level = level;
        this.kickingNick = optKickingUser.transform(ChannelUser::getNick).orNull();
        this.kickingNickString = kickingNickString;
        this.reason = reason;
    }
}
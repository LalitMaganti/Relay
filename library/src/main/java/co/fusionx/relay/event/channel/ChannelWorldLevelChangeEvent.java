package co.fusionx.relay.event.channel;

import com.google.common.base.Optional;

import co.fusionx.relay.base.Channel;
import co.fusionx.relay.base.ChannelUser;
import co.fusionx.relay.constants.UserLevel;

public class ChannelWorldLevelChangeEvent extends ChannelWorldUserEvent {

    public final String rawMode;

    public final Optional<? extends ChannelUser> changingUser;

    public final String changingNick;

    public final UserLevel level;

    public ChannelWorldLevelChangeEvent(final Channel channel, String rawMode,
            final ChannelUser user, final UserLevel level,
            final Optional<? extends ChannelUser> changingUser,
            final String changingNick) {
        super(channel, user);

        this.rawMode = rawMode;
        this.level = level;
        this.changingUser = changingUser;
        this.changingNick = changingNick;
    }
}
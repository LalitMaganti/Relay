package co.fusionx.relay.event.channel;

import com.google.common.base.Optional;

import co.fusionx.relay.Channel;
import co.fusionx.relay.ChannelUser;
import co.fusionx.relay.RelayMainUser;
import co.fusionx.relay.constants.UserLevel;

public class ChannelUserLevelChangeEvent extends ChannelEvent {

    public final UserLevel level;

    public final String rawMode;

    public final RelayMainUser user;

    public final Optional<? extends ChannelUser> changingUser;

    public final String changingNick;

    public ChannelUserLevelChangeEvent(final Channel channel, final String rawMode,
            final RelayMainUser user, final UserLevel level,
            final Optional<? extends ChannelUser> changingUser,
            final String changingNick) {
        super(channel);

        this.rawMode = rawMode;
        this.level = level;
        this.user = user;
        this.changingUser = changingUser;
        this.changingNick = changingNick;
    }
}
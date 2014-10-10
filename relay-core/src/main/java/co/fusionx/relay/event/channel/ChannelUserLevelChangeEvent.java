package co.fusionx.relay.event.channel;

import com.google.common.base.Optional;

import co.fusionx.relay.conversation.Channel;
import co.fusionx.relay.core.ChannelUser;
import co.fusionx.relay.constant.UserLevel;
import co.fusionx.relay.internal.core.InternalLibraryUser;

public class ChannelUserLevelChangeEvent extends ChannelEvent {

    public final UserLevel oldLevel;

    public final UserLevel newLevel;

    public final String rawMode;

    public final InternalLibraryUser user;

    public final Optional<? extends ChannelUser> changingUser;

    public final String changingNick;

    public ChannelUserLevelChangeEvent(final Channel channel, final String rawMode,
            final InternalLibraryUser user, final UserLevel oldLevel, final UserLevel newLevel,
            final Optional<? extends ChannelUser> changingUser,
            final String changingNick) {
        super(channel);

        this.rawMode = rawMode;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
        this.user = user;
        this.changingUser = changingUser;
        this.changingNick = changingNick;
    }
}
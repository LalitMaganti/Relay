package co.fusionx.relay.event.channel;

import com.google.common.base.Optional;

import co.fusionx.relay.conversation.Channel;
import co.fusionx.relay.core.ChannelUser;
import co.fusionx.relay.constant.UserLevel;

public class ChannelWorldQuitEvent extends ChannelWorldUserEvent {

    public final Optional<String> reason;

    public final UserLevel level;

    public ChannelWorldQuitEvent(final Channel channel, final ChannelUser user,
            final UserLevel level, final Optional<String> reason) {
        super(channel, user);

        this.level = level;
        this.reason = reason;
    }
}
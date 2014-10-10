package co.fusionx.relay.event.channel;

import com.google.common.base.Optional;

import co.fusionx.relay.conversation.Channel;
import co.fusionx.relay.core.ChannelUser;
import co.fusionx.relay.constant.UserLevel;

public class ChannelWorldPartEvent extends ChannelWorldUserEvent {

    public final Optional<String > optionalReason;

    public final UserLevel level;

    public ChannelWorldPartEvent(final Channel channel, final ChannelUser user,
            final UserLevel level, final Optional<String> optionalReason) {
        super(channel, user);

        this.level = level;
        this.optionalReason = optionalReason;
    }
}
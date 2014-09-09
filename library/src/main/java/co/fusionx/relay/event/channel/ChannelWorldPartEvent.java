package co.fusionx.relay.event.channel;

import co.fusionx.relay.conversation.Channel;
import co.fusionx.relay.core.ChannelUser;
import co.fusionx.relay.constants.UserLevel;

public class ChannelWorldPartEvent extends ChannelWorldUserEvent {

    public final String reason;

    public final UserLevel level;

    public ChannelWorldPartEvent(final Channel channel, final ChannelUser user,
            final UserLevel level, final String reason) {
        super(channel, user);

        this.level = level;
        this.reason = reason;
    }
}
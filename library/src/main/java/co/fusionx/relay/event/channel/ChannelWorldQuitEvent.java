package co.fusionx.relay.event.channel;

import co.fusionx.relay.base.Channel;
import co.fusionx.relay.base.ChannelUser;

public class ChannelWorldQuitEvent extends ChannelWorldUserEvent {

    public final String reason;

    public ChannelWorldQuitEvent(final Channel channel, final ChannelUser user,
            final String reason) {
        super(channel, user);

        this.reason = reason;
    }
}
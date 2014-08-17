package co.fusionx.relay.event.channel;

import co.fusionx.relay.base.Channel;
import co.fusionx.relay.base.ChannelUser;

public class ChannelWorldJoinEvent extends ChannelWorldUserEvent {

    public ChannelWorldJoinEvent(final Channel channel, final ChannelUser user) {
        super(channel, user);
    }
}
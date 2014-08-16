package co.fusionx.relay.event.channel;

import co.fusionx.relay.Channel;
import co.fusionx.relay.ChannelUser;

public class ChannelWorldJoinEvent extends ChannelWorldUserEvent {

    public ChannelWorldJoinEvent(final Channel channel, final ChannelUser user) {
        super(channel, user);
    }
}
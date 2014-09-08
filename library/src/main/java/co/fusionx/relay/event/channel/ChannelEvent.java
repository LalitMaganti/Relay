package co.fusionx.relay.event.channel;

import co.fusionx.relay.base.Channel;
import co.fusionx.relay.event.Event;

public abstract class ChannelEvent extends Event<Channel, ChannelEvent> {

    ChannelEvent(final Channel channel) {
        super(channel);
    }
}
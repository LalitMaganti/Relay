package co.fusionx.relay.event.channel;

import co.fusionx.relay.conversation.Channel;
import co.fusionx.relay.event.Event;

public abstract class ChannelEvent extends Event<Channel, ChannelEvent> {

    ChannelEvent(final Channel channel) {
        super(channel);
    }
}
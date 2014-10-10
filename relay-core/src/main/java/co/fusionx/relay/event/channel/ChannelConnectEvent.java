package co.fusionx.relay.event.channel;

import co.fusionx.relay.conversation.Channel;

public class ChannelConnectEvent extends ChannelEvent {

    public ChannelConnectEvent(final Channel channel) {
        super(channel);
    }
}
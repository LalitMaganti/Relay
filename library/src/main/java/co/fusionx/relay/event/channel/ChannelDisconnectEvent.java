package co.fusionx.relay.event.channel;

import co.fusionx.relay.Channel;

public class ChannelDisconnectEvent extends ChannelEvent {

    public final String message;

    public ChannelDisconnectEvent(final Channel channel, final String message) {
        super(channel);
        this.message = message;
    }
}